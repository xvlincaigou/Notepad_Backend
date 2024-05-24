from django.urls import path

from . import views

urlpatterns = [
    path("", views.index, name="index"),
    path("get_csrf_token", views.get_csrf_token, name="get_csrf_token"),
    path("register", views.register, name="register"),
    path("login", views.login, name="login"),
    path("changePassword", views.changePassword, name="changePassword"),
    path("changeUsername", views.changeUsername, name="changeUsername"),
    path("changeAvatar", views.changeAvatar, name="changeAvatar"),
    path("changePersonalSignature", views.changePersonalSignature, name="changePersonalSignature"),
    path("chatGLM", views.chatGLM, name="chatGLM"),
    path("createNote", views.createNote, name="createNote"),
]