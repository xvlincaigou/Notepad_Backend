from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
import json

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

"""
@brief: 用户注册
@param: username: 用户名 password: 密码
@return: userID: 用户ID
@date: 24/5/8
"""
@json_body_required
def register(request):
    data = request.json_body
    username = data.get('username')
    password = data.get('password')
    pass

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
def changePassword(request):
    data = request.json_body
    userID = data.get('userID')
    oldPassword = data.get('oldPassword')
    newPassword = data.get('newPassword')
    pass

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