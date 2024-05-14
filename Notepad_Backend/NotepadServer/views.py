from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.core.exceptions import ObjectDoesNotExist
from django.middleware.csrf import get_token

import json
import hashlib
from datetime import datetime
import os

from .models import File, User, Note, Token

# Create your views here.
def index(request):
    return HttpResponse("Hello, world. You're at the NotepadServer index.")

def json_body_required(func):
    def wrapper(request, *args, **kwargs):
        try:
            request.json_body = json.loads(request.body)
        except ValueError:
            return JsonResponse({'error': 'Invalid JSON'}, status=400)
        return func(request, *args, **kwargs)
    return wrapper

def token_required(func):
    def wrapper(request, *args, **kwargs):
        token = request.META.get('HTTP_AUTHORIZATION')
        print(token)
        if not token:
            return JsonResponse({'error': 'Token required'}, status=401)
        try:
            token = Token.objects.get(token=token)
        except ObjectDoesNotExist:
            return JsonResponse({'error': 'Invalid token'}, status=401)
        request.user = token.user
        return func(request, *args, **kwargs)
    return wrapper

def get_csrf_token(request):
    return JsonResponse({'csrf_token': get_token(request)})

"""
@brief: 用户注册，用户只需要提供用户名和密码，而用户唯一的ID是由服务器生成的，会在注册成功后返回给用户
@param: username: 用户名 password: 密码
@return: userID: 用户ID
@date: 24/5/8
"""
@json_body_required
def register(request):
    # 获取请求的username和password
    data = request.json_body
    username = data.get('username')
    password = data.get('password')

    # 生成userID
    now = datetime.now()
    hash_object = hashlib.sha1((username + password + now.strftime("%Y-%m-%d %H:%M:%S")).encode())
    hex_dig = hash_object.hexdigest()

    # 向数据库里面写入数据，这里可以暂时不写入头像和个性签名，用null代替
    user = User(userID=hex_dig[:8], username=username, password=password)
    user.save()

    # 创建一个新的令牌
    token = hashlib.sha256(os.urandom(60)).hexdigest()
    Token.objects.create(user=user, token=token)

    # 返回userID
    return JsonResponse({'userID': user.userID, 'token': token})

"""
@brief: 用户登录
@param: userID: 用户名 password: 密码
@return: userID: 用户ID
@date: 24/5/8
"""
@json_body_required
def login(request):
    data = request.json_body
    username = data.get('username')
    password = data.get('password')
    pass

"""
@brief: 修改用户密码
@param: userID: 用户ID oldPassword: 旧密码 newPassword: 新密码
@return: userID: 用户ID
@date: 24/5/8
"""
@json_body_required
@token_required
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
    return JsonResponse({'userID': user.userID}, status=200)

"""
@brief: 修改用户名
@param: userID: 用户ID newUsername: 新用户名
@return: none
@date: 24/5/8
"""
@json_body_required
def changeUsername(request):
    data = request.json_body
    userID = data.get('userID')
    newUsername = data.get('newUsername')
    pass

"""
@brief: 修改用户头像
@param: userID: 用户ID newAvatar: 新头像
@return: none
@date: 24/5/8
"""
@json_body_required
def changeAvatar(request):
    data = request.json_body
    userID = data.get('userID')
    newAvatar = data.get('newAvatar')
    pass

"""
@brief: 修改用户个性签名
@param: userID: 用户ID newPersonalSignature: 新个性签名
@return: none
@date: 24/5/8
"""
@json_body_required
def changePersonalSignature(request):
    data = request.json_body
    userID = data.get('userID')
    newPersonalSignature = data.get('newPersonalSignature')
    pass

"""
@brief: 创建新的笔记（有点怀疑这个到底需不需要，因为按理说笔记是在客户端创建的）
@param: userID: 用户ID title: 笔记标题 tip: 笔记的tip type: 笔记的类别 content: 笔记的内容
@return: noteID: 笔记ID
@date: 24/5/8
"""
@json_body_required
def createNote(request):
    data = request.json_body
    userID = data.get('userID')
    title = data.get('title')
    tip = data.get('tip')
    type = data.get('type')
    content = data.get('content')
    pass

"""
@brief: 删除笔记
@param: userID: 用户ID noteID: 笔记ID
@return: none
@date: 24/5/8
"""
@json_body_required
def deleteNote(request):
    data = request.json_body
    userID = data.get('userID')
    noteID = data.get('noteID')
    pass

"""
@brief: 修改笔记（似乎可以细分一下，不然的话把一个笔记全部传上去是不是太费流量了）
@param: userID: 用户ID noteID: 笔记ID title: 笔记标题 tip: 笔记的tip type: 笔记的类别 content: 笔记的内容
@return: none
@date: 24/5/8
"""
@json_body_required
def modifyNote(request):
    data = request.json_body
    userID = data.get('userID')
    noteID = data.get('noteID')
    title = data.get('title')
    tip = data.get('tip')
    type = data.get('type')
    content = data.get('content')
    pass

"""
@brief: 同步上传笔记
@param: userID: 用户ID noteID: 笔记列表
@return: none
@date: 24/5/8
"""
@json_body_required
def syncUpload(request):
    data = request.json_body
    userID = data.get('userID')
    noteList = data.get('noteList')
    pass

"""
@brief: 同步下载笔记
@param: userID: 用户ID
@return: noteList: 笔记列表
@date: 24/5/8
"""
def syncDownload(request):
    data = request.json_body
    userID = data.get('userID')
    pass