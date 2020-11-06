from todo_app.app import db

def test_index(client):
    res = client.get('/')
    assert res.status_code == 200
    # assert False

def test_404(client):
    res = client.get('/bad-url-that-doesnt-exist')
    assert res.status_code == 404
    assert False