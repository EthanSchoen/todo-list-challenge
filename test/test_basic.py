import json
from todo_app.app import Tasks
from sqlalchemy import MetaData


#########################
######## Tests ##########
#########################
def test_404(client):
    res = client.get('/bad-url-that-doesnt-exist')
    assert res.status_code == 404

def test_index(client):
    res = client.get('/')
    assert res.status_code == 302
    res = client.post('/user/register', json={
        'username': 'test_username', 'password': 'Password123'
    }, follow_redirects=False)
    assert res.status_code == 302
    res = client.get('/')
    assert res.status_code == 200

    print(res.get_data(as_text=True))
    assert False
    

def test_add_task(client):
    add_task_helper(client, 1, 'This is the first task to be added.', True)
    add_task_helper(client, 2, 'This is the second task to be added.', True)
    add_task_helper(client, 3, 'This is the third task to be added.', True)
    add_task_helper(client, 3, '', False)
    add_task_helper(client, 3, ' ', False)
    add_task_helper(client, 3, '\t', False)
    add_task_helper(client, 4, 'This is the forth task to be added.', True)

def add_task_helper(client, id,  value, is_valid):
    res = add_task(client, value)
    assert len(Tasks.query.all()) == id
    if is_valid:
        assert res.get_data(as_text=True).find(value) > -1

def test_remove_task(client):
    add_task(client, 'task 1')
    add_task(client, 'task 2')
    add_task(client, 'task 3')
    add_task(client, 'task 4')

    # remove task 1 (ID = 1)
    res = client.post('/remove', json={ 'ID': 1 }, follow_redirects=True)
    assert res.status_code == 200
    assert Tasks.query.filter_by(task='task 1').all() == []
    assert not Tasks.query.filter_by(task='task 2').all() == []
    assert not Tasks.query.filter_by(task='task 3').all() == []
    assert not Tasks.query.filter_by(task='task 4').all() == []

    # remove task 3 (ID = 3)
    res = client.post('/remove', json={ 'ID': 3 }, follow_redirects=True)
    assert res.status_code == 200
    assert Tasks.query.filter_by(task='task 3').all() == []

def test_edit_task(client):
    add_task(client, 'task 1')

    # edit task (ID = 1)
    res = client.post('/edit', json={ 'ID': 1, 'task': 'task edited'}, follow_redirects=False)
    assert res.status_code == 302
    res = client.get('/')
    assert res.status_code == 200

    # check if task is edited
    assert not Tasks.query.filter_by(task='task edited').all() == []
    assert res.get_data(as_text=True).find("task edited") > -1

    # confirm pre-edited task can't be found
    assert Tasks.query.filter_by(task='task 1').all() == []
    assert res.get_data(as_text=True).find("task 1") == -1

def test_complete_task(client):
    res = add_task(client, 'task 1')
    # make sure task starts out not completed
    assert Tasks.query.filter_by(complete=True).all() == []
    assert res.get_data(as_text=True).find('style=text-decoration:line-through;') == -1

    # edit task (ID = 1)
    res = client.post('/complete', json={ 'ID': 1, 'complete': True }, follow_redirects=False)
    assert res.status_code == 302
    res = client.get('/')
    assert res.status_code == 200

    # check if task is completed
    assert not Tasks.query.filter_by(id=1, complete=True).all() == []
    assert res.get_data(as_text=True).find('style=text-decoration:line-through;') > -1

#########################
#### Helper Methods #####
#########################
def add_task(client, value):
    res = client.post('/add', data=dict(
        newtask = value
    ), follow_redirects=False)
    assert res.status_code == 302
    res = client.get('/')
    assert res.status_code == 200
    return res

# method to add tasks to test db if uncommented in conftest.py so URI in app.py can be
#   set to test_todo.db to open app with data made by tests
def post_test_init_db(client):
    client.post('/add', data=dict(
        newtask = 'Here is task 1'
    ), follow_redirects=False)
    client.post('/add', data=dict(
        newtask = 'Here is task 2'
    ), follow_redirects=False)
    client.post('/add', data=dict(
        newtask = 'Here is task 3'
    ), follow_redirects=False)