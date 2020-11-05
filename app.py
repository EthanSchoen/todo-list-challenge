from flask import Flask, render_template, request, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
from flask_user import login_required, UserManager, UserMixin, SQLAlchemyAdapter

app = Flask(__name__)
app.config['SECRET_KEY'] = 'thisisasecretkeyiguess'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data/todo.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['CSRF_ENABLED'] = True
app.config['USER_ENABLE_EMAIL'] = False
db = SQLAlchemy(app)

class User(db.Model, UserMixin):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), nullable=False, unique=False)
    password = db.Column(db.String(255), nullable=False, unique=False, server_default='')
    active = db.Column(db.Boolean(), nullable=False, server_default='0')


class Tasks(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user = db.Column(db.String(50), nullable=False, unique=False)
    task = db.Column(db.String(200), unique=False, nullable=False)
    complete = db.Column(db.Boolean, unique=False, default=False)

    def __repr__(self):
        return '<Task %r>' % self.task

db_adapter = SQLAlchemyAdapter(db, User)
user_manager = UserManager(db_adapter, app)

@app.route('/', methods=['GET'])
@login_required
def index():
    taskList = Tasks.query.all()
    return render_template('index.html.j2', tasks=taskList)

@app.route('/add', methods=['POST'])
def add():
    if not request.form['newtask'].strip() == '':
        newTask = Tasks(task=request.form['newtask'])
        db.session.add(newTask)
        db.session.commit()
    return redirect(url_for('index'))

@app.route('/remove', methods=['POST'])
def remove():
    Tasks.query.filter_by(id=request.get_json()['ID']).delete()
    db.session.commit()
    return redirect(url_for('index'))

@app.route('/edit', methods=['POST'])
def edit():
    taskID = request.get_json()['ID']
    editedTask = request.get_json()['task']
    Tasks.query.filter_by(id=taskID).first().task = editedTask
    db.session.commit()
    return redirect(url_for('index'))

@app.route('/complete', methods=['POST'])
def complete():
    print(request.get_json())
    Tasks.query.filter_by(id=request.get_json()['ID']).first().complete = request.get_json()['complete']
    db.session.commit()
    return redirect(url_for('index'))

if __name__ == '__main__':
    app.run()