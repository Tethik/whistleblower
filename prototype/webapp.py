import os
from flask import Flask, request, redirect, url_for, render_template, send_from_directory, jsonify
from werkzeug import secure_filename
import uuid

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'upload/'
app.config['REPLY_FOLDER'] = 'replies/'
app.config['CLIENT_FOLDER'] = 'client/'

def upload_file_to_folder(folder, filename):
    content = request.form['content']

    if content:
        file = open(os.path.join(folder, filename), "w+")
        file.write(content)
        file.close()
        return True
    return False



@app.route('/api/reply_to/<codename>', methods=['POST'])
def reply(codename):
    codename = "".join(codename.strip().replace("+", "").split(" "))
    filename = secure_filename(codename + ".json")
    print(filename)
    if upload_file_to_folder(app.config['REPLY_FOLDER'], filename):
        return "Ok"
    return "Content is empty", 405

@app.route('/api/fetch_reply/<codename>', methods=['GET'])
def fetch_reply(codename):
    codename = "".join(codename.strip().replace("+", "").split(" "))
    filename = secure_filename(codename + ".json")
    return send_from_directory(app.config['REPLY_FOLDER'], filename)

@app.route('/api/submit', methods=['POST'])
def submit():
    id = str(uuid.uuid4())
    filename = secure_filename(id+".asc")
    if upload_file_to_folder(app.config['UPLOAD_FOLDER'], filename):
        return id
    return "Content is empty", 405
    # content = request.form['content']
    #
    # if content:
    #     filename = secure_filename(id+".asc")
    #     file = open(os.path.join(app.config['UPLOAD_FOLDER'], filename), "w+")
    #     file.write(content)
    #     file.close()
    #     return id
    # else:
    #     return "Content is empty", 405


@app.route('/api/submissions', methods=['GET'])
def list_submissions():
    files = os.listdir(app.config['UPLOAD_FOLDER'])
    files.remove(".gitignore")
    return jsonify(files=sorted(files, key=lambda f:
        os.stat(os.path.join(app.config['UPLOAD_FOLDER'],f)).st_mtime, reverse=True))

@app.route('/api/submissions/<path:path>', methods=['GET'])
def fetch_submission(path):
    return send_from_directory(app.config['UPLOAD_FOLDER'], path)

@app.route('/')
def default():
    return serve_client('index.html')

@app.route('/<path:path>')
def serve_client(path):
    return send_from_directory(app.config['CLIENT_FOLDER'], path)

if __name__ == "__main__":
    app.run(port=5011, debug=True)
