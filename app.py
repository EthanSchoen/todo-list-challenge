from flask import Flask, render_template, request, redirect, url_for
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data/todo.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = 'False'
db = SQLAlchemy(app)

class Tasks(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    task = db.Column(db.String(200), unique=False, nullable=False)
    complete = db.Column(db.Boolean, unique=False, default=False)

    def __repr__(self):
        return '<Task %r>' % self.task

@app.route('/', methods=['GET'])
def index():
    taskList = Tasks.query.all()
    return render_template('index.html', tasks=taskList)

@app.route('/add', methods=['POST'])
def add():
    if not request.form['newtask'].strip() == '':
        newTask = Tasks(task=request.form['newtask'])
        db.session.add(newTask)
        db.session.commit()
    return redirect(url_for('index'))

if __name__ == '__main__':
    app.run()