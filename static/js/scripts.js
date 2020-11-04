function addTask(taskName) {
    if (taskName.trim() == "") {  // do nothing if input is whitespace or empty
        return;
    }

    var table = document.getElementById("list");
    var UID = Date.now();  // lazy UID for rows
    var row = table.insertRow(-1);

    row.setAttribute("id", UID); // give each row an id number

    var newtask = document.createElement("td");
    newtask.innerHTML = taskName;
    row.appendChild(newtask);

    var operations = document.createElement("td");
    var deleteButton = document.createElement("input");
    deleteButton.setAttribute("type", "button");
    deleteButton.setAttribute("value", "Delete Task");
    deleteButton.setAttribute("onclick", "deleteRow("+UID+")");
    operations.appendChild(deleteButton);
    row.appendChild(operations);

    document.getElementById('newtask').value = "";
}

function deleteRow(rowNumber){
    document.getElementById(rowNumber).remove();
}

function lookforenter(ele) {
    if(event.key === 'Enter') {
        event.preventDefault();
        addTask(document.getElementById('newtask').value);
    }
}