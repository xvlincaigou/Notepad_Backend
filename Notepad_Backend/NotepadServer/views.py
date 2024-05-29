from django.http import HttpResponse, JsonResponse, FileResponse
from django.core.exceptions import ObjectDoesNotExist
from django.middleware.csrf import get_token
from django.core.files.storage import default_storage
from django.utils import timezone
from django.views.decorators.csrf import csrf_exempt

import json
import hashlib
from datetime import datetime, timedelta
import os
import shutil
from zhipuai import ZhipuAI

from .models import User, Note
from Notepad_Backend.settings import BASE_DIR

# Create your views here.
def index(request):
    return HttpResponse("Hello, world. You're at the NotepadServer index.")

# 用于装饰器，检查请求的body是否是json格式
def json_body_required(func):
    def wrapper(request, *args, **kwargs):
        try:
            request.json_body = json.loads(request.body)
        except ValueError:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)
        return func(request, *args, **kwargs)
    return wrapper

# 用于装饰器，检查请求的header的token是否有效
def token_required(func):
    def wrapper(request, *args, **kwargs):
        token = request.META.get('HTTP_AUTHORIZATION')
        # 如果没有token，返回错误
        if not token:
            return JsonResponse({'error': 'Token required'}, status=401)
        # 如果token不在数据库里面，返回错误
        try:
            user = User.objects.get(token=token)
        except ObjectDoesNotExist:
            return JsonResponse({'error': 'Invalid token'}, status=401)
        # 如果token过期，返回错误
        if user.lastLoginTime < timezone.now() - timedelta(days=30):
            return JsonResponse({'error': 'Token expired'}, status=401)
        return func(request, *args, **kwargs)
    return wrapper

"""
@brief: 用户注册，用户只需要提供用户名和密码，而用户唯一的ID是由服务器生成的，会在注册成功后返回给用户
@param: username: 用户名 password: 密码
@return: userID: 用户ID
@date: 24/5/19
"""
@json_body_required
@csrf_exempt
def register(request):
    # 获取请求的username和password
    data = request.json_body
    print(data)
    username = data.get('username')
    password = data.get('password')

    # 生成userID
    now = datetime.now()
    hash_object = hashlib.sha1((username + password + now.strftime("%Y-%m-%d %H:%M:%S")).encode())
    hex_dig = hash_object.hexdigest()

    # 向数据库里面写入数据，这里可以暂时不写入头像和个性签名和token，用null代替
    user = User(userID=hex_dig[:8], username=username, password=password, lastLoginTime=timezone.now() - timedelta(days=8))
    user.save()

    # 返回userID
    return JsonResponse({'userID': user.userID})

"""
@brief: 用户登录
@param: userID: 用户名 password: 密码
@return: 用户的基本信息和笔记列表，用户得到笔记列表之后会向服务器发起请求，下载对应的笔记
@date: 24/5/26
"""
@json_body_required
@csrf_exempt
def login(request):
    data = request.json_body
    userID = data.get('userID')
    password = data.get('password')

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    if user.password != password:
        return JsonResponse({'error': 'Invalid password'}, status=404)
    
    # 创建一个新的令牌
    token = hashlib.sha256(os.urandom(60)).hexdigest()
    user.token = token
    
    # 更新最后登录时间
    user.lastLoginTime = timezone.now()
    user.save()

    # 返回用户信息 
    userNoteList = list(user.notes.values())
    return JsonResponse({'userID': user.userID, 'username': user.username, 'personalSignature': user.personalSignature, 'noteList': userNoteList, 'token': token})

"""
@brief: 获取用户头像
@param: userID: 用户ID
@return: 用户头像
@date: 24/5/26
"""
@token_required
@json_body_required
@csrf_exempt
def getAvatar(request):
    data = request.json_body
    userID = data.get('userID')
    
    avatar_directory = os.path.join(BASE_DIR, 'userData', userID, 'avatar')
    try:
        avatar_filename = os.listdir(avatar_directory)[0]  # Get the first file in the directory
        avatar_path = os.path.join(avatar_directory, avatar_filename)
        avatar_file = open(avatar_path, 'rb')
    except (FileNotFoundError, IndexError):
        return JsonResponse({'error': 'Avatar not found'}, status=404)

    return FileResponse(avatar_file, as_attachment=True, filename=avatar_filename)
    
"""
@brief: 修改用户密码
@param: userID: 用户ID oldPassword: 旧密码 newPassword: 新密码
@return: message: 操作成功与否的信息
@date: 24/5/24
"""
@json_body_required
@token_required
@csrf_exempt
def changePassword(request):
    data = request.json_body
    userID = data.get('userID')
    oldPassword = data.get('oldPassword')
    newPassword = data.get('newPassword')

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)

    if user.password != oldPassword:
        return JsonResponse({'error': 'Invalid password'}, status=404)
    user.password = newPassword
    user.save()
    return JsonResponse({'message': 'Success'}, status=200)

"""
@brief: 修改用户名
@param: userID: 用户ID newUsername: 新用户名
@return: message: 操作成功与否的信息
@date: 24/5/8
"""
@json_body_required
@token_required
@csrf_exempt
def changeUsername(request):
    data = request.json_body
    userID = data.get('userID')
    newUsername = data.get('newUsername')

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    user.username = newUsername
    user.save()
    return JsonResponse({'message': 'Success'}, status=200)

"""
@brief: 修改用户头像
@param: userID: 用户ID newAvatar: 新头像
@return: message: 操作成功与否的信息
@date: 24/5/29
"""
@token_required
@csrf_exempt
def changeAvatar(request):
    data_string = request.POST['json']
    data = json.loads(data_string)
    userID = data['userID']

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)

    if 'newAvatar' not in request.FILES:
        return JsonResponse({'error': 'No file uploaded'}, status=400)

    file = request.FILES['newAvatar']
    directory = os.path.join(BASE_DIR, 'userData', userID, 'avatar')
    if not os.path.exists(directory):
        os.makedirs(directory)
    else:
        files = os.listdir(directory)
        for single_file in files:
            os.remove(os.path.join(directory, single_file))

    file_name = default_storage.save(os.path.join(directory, file.name), file)
    file_url = default_storage.url(file_name)

    user.avatar = file_url
    user.save()

    return JsonResponse({'message': 'Success'}, status=200)
    
"""
@brief: 修改用户个性签名
@param: userID: 用户ID newPersonalSignature: 新个性签名
@return: message: 操作成功与否的信息
@date: 24/5/24
"""
@json_body_required
@csrf_exempt
def changePersonalSignature(request):
    data = request.json_body
    userID = data.get('userID')
    newPersonalSignature = data.get('newPersonalSignature')

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    user.personalSignature = newPersonalSignature
    user.save()
    return JsonResponse({'message': 'Success'}, status=200)

"""
@brief: 创建（上传）新的笔记
@param: userID: 用户ID title: 笔记标题 type: 笔记的类别 parentDirectory:笔记在本地所在的文件夹名 uploadFileListJson: 笔记的内容
@return: message: 操作成功与否的信息
@date: 24/5/29
"""
@token_required
@csrf_exempt
def createNote(request):
    data_string = request.POST['json']
    data = json.loads(data_string)
    userID = data['userID']
    title = data['title']
    type = data['type']
    parentDirectory = data['parentDirectory']
    uploadFileListJson = data['uploadFileListJson']

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    try:
        parts = os.path.split(parentDirectory)
        full_parent_directory = os.path.join(BASE_DIR, parts[0], str(userID), parts[1])
        os.makedirs(full_parent_directory, exist_ok=True)
    except FileNotFoundError:
        return JsonResponse({'error': 'Parent directory does not exist'}, status=404)

    try:
        note = Note(title=title, type=type, author=user, lastSaveToCloudTime=timezone.now(), demosticId=int(parts[1]))
        note.file = uploadFileListJson
        files = request.FILES.getlist('file')
        
        file_index = 0
        for item in note.file:
            if item['type'] != 'text':
                print(item['type'])
                save_path = os.path.join(full_parent_directory, files[file_index].name)
                with open(save_path,'wb+') as f:
                    f.write(files[file_index].read())
                item['content'] = save_path
                file_index += 1
        
        note.save()
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

    return JsonResponse({'Message': 'OKay'}, status=200)

"""
@brief: 删除笔记
@param: userID: 用户ID noteID: 笔记ID
@return: message: 操作成功与否的信息
@date: 24/5/25
"""
@json_body_required
@token_required
@csrf_exempt
def deleteNote(request):
    data = request.json_body
    userID = data.get('userID')
    demosticId = data.get('demosticId')

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    try:
        note = user.notes.get(demosticId=demosticId)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'Note with given demosticId does not exist'}, status=404)
    
    note.delete()
    try:
        note_directory = os.path.join(BASE_DIR, 'userData', userID, demosticId)
        shutil.rmtree(note_directory)
    except FileNotFoundError:
        return JsonResponse({'error': 'Note directory does not exist'}, status=404)

    return JsonResponse({'message': 'Success'}, status=200)

"""
@brief: 修改笔记（似乎可以细分一下，不然的话把一个笔记全部传上去是不是太费流量了）
@param: userID: 用户ID demosticId: 笔记ID title: 笔记标题 type: 笔记的类别 content: 笔记的内容
@return: message: 操作成功与否的信息
@date: 24/5/25
"""
@token_required
@csrf_exempt
def modifyNote(request):
    data_string = request.POST['json']
    data = json.loads(data_string)
    userID = data['userID']
    demosticId = data.get('demosticId')
    title = data['title']
    type = data['type']

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    try:
        note = user.notes.get(demosticId=demosticId)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'Note with given demosticId does not exist'}, status=404)
    
    full_parent_directory = os.path.join(BASE_DIR, 'userData', str(userID), demosticId)

    try:
        # 修改对应的title和type
        note.title = title
        note.type = type
        # 把对应的文件更新
        files = request.FILES.getlist('file')
        for file in files:
            save_path = os.path.join(full_parent_directory, file.name)
            with open(save_path,'wb+') as f:
                f.write(file.read())
            note.file[file.name] = save_path
        f.close()
        # 修改时间
        note.lastSaveToCloudTime = timezone.now()
        note.save()
    except Exception as e:
        return JsonResponse({'error': str(e)}, status=500)

    return JsonResponse({'Message': 'OKay'}, status=200)

"""
@brief: 同步下载笔记
@param: userID: 用户ID, filename: 笔记文件名
@return: note: 笔记内容
@date: 24/5/26
"""
@json_body_required
@token_required
@csrf_exempt
def syncDownload(request):
    data = request.json_body
    userID = data.get('userID')
    demosticId = data.get('demosticId')
    filename = data.get('filename')

    try:
        user = User.objects.get(userID=userID)
    except ObjectDoesNotExist:
        return JsonResponse({'error': 'User with given userID does not exist'}, status=404)
    
    file_path = os.path.join(BASE_DIR, 'userData', userID, str(demosticId), filename)
    if os.path.exists(file_path):
        with open(file_path, 'rb') as f:
            response = HttpResponse(f.read(), content_type='application/octet-stream')
            response['Content-Disposition'] = 'attachment; filename="{}"'.format(filename)
            return response
    else:
        return HttpResponse("File not found", status=404)

"""
@brief: 与智谱清言API的交互，对于笔记进行处理
@param: messages: 用户输入的对话
@return: answer: 机器回答
@date: 24/5/15
"""
@json_body_required
@csrf_exempt
def chatGLM(request):
    data = request.json_body
    message = data.get('message')

    try:
        config_json_path = os.path.join(BASE_DIR, 'config.json')
        with open(config_json_path, 'r') as f:
            config = json.load(f)
        zhipuAPI_key = config['zhipuAPI_key']
    except FileNotFoundError:
        return JsonResponse({'error': 'Config file not found'}, status=404)
    except KeyError:
        return JsonResponse({'error': 'zhipuAPI_key not found in config file'}, status=404)

    client = ZhipuAI(api_key=zhipuAPI_key)
    response = client.chat.completions.create(
    model="glm-4",  
        messages=[
            {"role": "user", "content": message},
        ],
        stream=True,
        )
    
    answer = ""
    for chunk in response:
        answer += chunk.choices[0].delta.content
    
    return JsonResponse({'answer': answer}, status=200)