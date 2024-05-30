from django.urls import path

from . import views

urlpatterns = [
    path("", views.index, name="index"),
    path("register", views.register, name="register"),
    path("login", views.login, name="login"),
    path("getAvatar", views.getAvatar, name="getAvatar"),
    path("changePassword", views.changePassword, name="changePassword"),
    path("changeUsername", views.changeUsername, name="changeUsername"),
    path("changeAvatar", views.changeAvatar, name="changeAvatar"),
    path("changePersonalSignature", views.changePersonalSignature, name="changePersonalSignature"),
    path("chatGLM", views.chatGLM, name="chatGLM"),
    path("createNote", views.createNote, name="createNote"),
    path("deleteNote", views.deleteNote, name="deleteNote"),
    path("syncDownload", views.syncDownload, name="syncDownload"),
]