# Generated by Django 5.0.4 on 2024-05-19 08:27

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('NotepadServer', '0004_alter_note_author_alter_user_avatar'),
    ]

    operations = [
        migrations.AddField(
            model_name='user',
            name='lastLoginTime',
            field=models.DateTimeField(null=True),
        ),
        migrations.AddField(
            model_name='user',
            name='token',
            field=models.CharField(max_length=64, null=True),
        ),
        migrations.DeleteModel(
            name='Token',
        ),
    ]