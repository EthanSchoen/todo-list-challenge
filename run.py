import os
from todo_app.app import db

# create data folder if it doesn't exist
if not os.path.isdir('todo_app/data'):
    os.mkdir('todo_app/data')

# unit tests
os.system('py.test')

# create database if it doesn't exist
db.create_all()

print()
print("Loading Todo app...")
# run app
os.system('python todo_app/app.py')