from django.db import models

# Create your models here.
class User(models.Model):
    # 用户ID
    userID = models.CharField(max_length=8, default='00000000')
    # 用户名
    username = models.CharField(max_length=30)
    # 密码
    password = models.CharField(max_length=30)
    # 头像
    avatar = models.CharField(max_length=60, null=True)
    # 个性签名
    personalSignature = models.CharField(max_length=100, null=True)
    # token
    token = models.CharField(max_length=64, null=True)
    # 上次登录时间
    lastLoginTime = models.DateTimeField(null=True)
    # 个性化推荐内容
    personalizedRecommendation = models.TextField(null=True)

class Note(models.Model):
    # 标题
    title = models.CharField(max_length=30)
    # 类型：inspiration、diary、todo、other
    type = models.CharField(max_length=10)
    # 笔记当中存储的所有文件的路径以及文字内容本身
    file = models.JSONField()
    # 作者，存储了作者的ID
    author = models.ForeignKey(User, on_delete=models.CASCADE, related_name='notes')
    # 最后保存到云端的时间
    lastSaveToCloudTime = models.DateTimeField()
    # 这个note在这个用户的笔记本里面的id
    demosticId = models.IntegerField(default=0)