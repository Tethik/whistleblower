
var selected_receivers = {};

// Files to upload. For now only single, but encoding/encrypting the same.
var files = [];

function sendFile(file, success_callback, error_callback) {
    var uri = "/";
    var xhr = new XMLHttpRequest();
    var fd = new FormData();

    xhr.open("POST", uri, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4) {
          if(xhr.status == 200) {
            // Handle response.
            console.log("Saved with id " + xhr.responseText)
            success_callback();
          } else {
            error_callback(xhr.responseText);
          }
        }
    };
    fd.append('content', file);
    // Initiate a multipart/form-data upload
    xhr.send(fd);
}

function upload() {
  hide("submission-form");
  show("progress");

  console.log(files.length);

  message = {
    files: files,
    short_title: document.getElementById("short-title").value,
    description:  document.getElementById("description").value,
  }

  raw_json = JSON.stringify(message);

  // Convert set of receivers to list of keys.
  receivers = { keys: [] };

  for(r in selected_receivers) {
    for(k in selected_receivers[r].keys) {
      receivers.keys.push(selected_receivers[r].keys[k]);
    }
  }


  console.log("Encrypting message for " + receivers.length + " receivers");
  receivers.keys.forEach(function(r) {console.log(r)});

  openpgp.encryptMessage(receivers.keys, raw_json)
   .then(function(pgpMessage) {
        // console.log(pgpMessage);
        // document.getElementById('uploaded-file').value = pgpMessage;
        // document.getElementById('form').submit();
        sendFile(pgpMessage, function() {
          hide("progress");
          show("done");
        }, function() {
          hide("progress");
          alert("Error occured! Something went wrong during submission. Sorry.");
          show("error");
        });
    }).catch(function(error) {
        console.log(error);
    });

  // console.log(raw_json);

  // To prevent form from submitting normally.
  return false;
}

function readFiles(e) {
  // Clear old files
  files = [];
  // var reader = new FileReader();
  // reader.onload = function(e) {
  //   var contents = e.target.result;
  //   files.push({ filename: file.name, binary: btoa(contents) });
  // };



  for(var i = 0; i < e.target.files.length; ++i) {
    var file = e.target.files[i];
    if (!file) {
      return;
    }

    (function(file) {
      var reader = new FileReader();
      reader.onload = function(e) {
        var contents = e.target.result;
        files.push({ filename: file.name, binary: btoa(contents) });
      };
      reader.readAsBinaryString(file);
    })(file);
  }

}

function addSelector(key) {
  var receivers_list = document.getElementById("receivers");
  var username = key.keys[0].users[0].userId.userid;

  var div = document.createElement("div");
  div.class = "checkbox";
  var new_elem = document.createElement("label");
  var input = document.createElement("input")
  input.type = "checkbox";
  input.checked = true;
  var t = document.createTextNode(username);
  new_elem.appendChild(input);
  new_elem.appendChild(t);
  div.appendChild(new_elem);
  receivers_list.appendChild(div);
  selected_receivers[username] = key;

  input.onchange = function(change) {
    // console.log(change);
    // console.log(change.target.checked);
    if(change.target.checked) {
      selected_receivers[username] = key;
    } else {
      delete selected_receivers[username];
    }
    console.log(selected_receivers);
  };
}

function show(id) {
  var elem = document.getElementById(id);
  elem.style.display = "block";
}

function hide(id) {
  var elem = document.getElementById(id);
  elem.style.display = "none";
}

function load() {
  // Render selection of keys.
  keys.forEach(function(key) {
    console.log(key);
    console.log(key.keys[0].users[0].userId.userid);
    addSelector(key);
  });

  // Register events.
  document.getElementById('file-input')
    .addEventListener('change', readFiles, false);
}
