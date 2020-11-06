import os
import unittest
from todo_app.app import db, app

TODO_DB = 'test_todo.db'

class BasicTests(unittest.TestCase):
 
    ############################
    #### setup and teardown ####
    ############################
 
    # executed prior to each test
    def setUp(self):
        app.config['TESTING'] = True
        app.config['WTF_CSRF_ENABLED'] = False
        app.config['DEBUG'] = False
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///../todo_app/data/' + TODO_DB
        self.app = app.test_client()
        db.drop_all()
        db.create_all()
 
        # Disable sending emails during unit testing
        # mail.init_app(app)
        self.assertEqual(app.debug, False)
 
    # executed after each test
    def tearDown(self):
        pass

    # executed beofre all tests
    def setUpClass():
        print('hi')

    # executed after all tests
    def tearDownClass():
        print('bye')

###############
#### tests ####
###############
    def test_main_page(self):
        # print('testing main')
        response = self.app.get('/', follow_redirects=True)
        self.assertEqual(response.status_code, 200)

    def test_number2(self):
        print('im numba 2')
 
if __name__ == "__main__":
    unittest.main(verbosity=2)