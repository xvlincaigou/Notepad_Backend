from django.db import models

import os

from Notepad_Backend.settings import BASE_DIR

# Create your models here.

class File(models.Model):
    # 在服务器上的路径
    path = models.CharField(max_length=100)
    # 类型：text、image、audio、video、other
    type = models.CharField(max_length=10)

def get_file_path(instance, filename):
    return os.path.join(BASE_DIR, 'userData', instance.userID, 'avatar', filename)

class User(models.Model):
    # 用户ID
    userID = models.CharField(max_length=30)
    # 用户名
    username = models.CharField(max_length=30)
    # 密码
    password = models.CharField(max_length=30)
    # 头像
    avatar = models.ImageField(upload_to=get_file_path, null=True)
    # 个性签名
    personalSignature = models.CharField(max_length=100, null=True)

class Note(models.Model):
    # 标题
    title = models.CharField(max_length=30)
    # 提示
    tip = models.CharField(max_length=100)
    # 类型：inspiration、diary、todo、other
    type = models.CharField(max_length=10)
    # 笔记当中存储的文件，但是这里要不要用foreign key呢？
    file = models.JSONField()
    # 作者，存储了作者的ID
    author = models.ForeignKey(User, on_delete=models.CASCADE, related_name='notes')
    # 创建时间
    createTime = models.DateTimeField()
    # 最后编辑时间
    lastEditTime = models.DateTimeField()
    # 最后保存到云端的时间
    lastSaveToCloudTime = models.DateTimeField()

class Token(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    token = models.CharField(max_length=64)