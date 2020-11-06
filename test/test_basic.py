import json
from todo_app.app import Tasks


#########################
######## Tests ##########
#########################
def test_404(client):
    res = client.get('/bad-url-that-doesnt-exist')
    assert res.status_code == 404

def test_index(client):
    res = client.get('/')
    assert res.status_code == 200

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
    add_task(client, 'this is the task to be added.')
    res = client.post('/remove', json={ 'ID': 1 }, follow_redirects=True)
    assert res.status_code == 200

def test_edit_task(client):
    add_task(client, 'this is the task to be added.')
    res = client.post('/edit', json={ 'ID': 1, 'task': 'The task is now edited'}, follow_redirects=True)
    assert res.status_code == 200

def test_complete_task(client):
    add_task(client, 'this is the task to be added.')
    res = client.post('/complete', json={ 'ID': 1, 'complete': True }, follow_redirects=True)
    assert res.status_code == 200

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