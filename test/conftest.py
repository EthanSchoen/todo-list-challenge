import os
import pytest
from todo_app.app import db, app as flask_app
from .test_basic import post_test_init_db

@pytest.fixture
def app():
    yield flask_app

@pytest.fixture
def client(app):
    return app.test_client()

@pytest.fixture(autouse=True)
# setup/teardown
def support():
    # executed prior to each test
    db.create_all()

    yield

    # executed after each test
    db.drop_all()

@pytest.fixture(scope="session", autouse=True)
def tearDownClass():
    # executed before all tests
    flask_app.config['TESTING'] = True
    flask_app.config['WTF_CSRF_ENABLED'] = False
    flask_app.config['DEBUG'] = False
    flask_app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///../todo_app/data/test_todo.db'

    yield

    # executed after all tests
    db.create_all()
    post_test_init_db(flask_app.test_client())
    # try:
    #     os.remove(os.path.abspath('todo_app/data/test_todo.db'))
    # except OSError:
    #     print('could not delete test_todo.db')
    #     pass