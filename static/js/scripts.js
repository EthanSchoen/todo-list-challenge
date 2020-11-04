function addTask(taskName) {
    if (taskName.trim() == "") {  // do nothing if input is whitespace or empty
        return;
    }

    var table = document.getElementById("list");
    var UID = Date.now();  // lazy UID for rows
    var row = table.insertRow(-1);

    row.setAttribute("id", UID); // give each row an id number

// User inputed task name
    var newtask = document.createElement("td");
    newtask.setAttribute("class", "taskcolumn");
    newtask.innerHTML = taskName;
    row.appendChild(newtask);

// Add buttons for task operations
    var operations = document.createElement("td");
    operations.setAttribute("class", "opcolumn");
// delete button
    var deleteButton = document.createElement("input");
    deleteButton.setAttribute("type", "button");
    deleteButton.setAttribute("value", "Remove");
    deleteButton.setAttribute("onclick", "deleteRow("+UID+")");
// edit button
    var editButton = document.createElement("input");
    editButton.setAttribute("type", "button");
    editButton.setAttribute("value", "Edit");
    editButton.setAttribute("onclick", "editRow("+UID+")");
// finish up row
    operations.appendChild(deleteButton);
    operations.appendChild(editButton);
    row.appendChild(operations);

// Clear input field
    document.getElementById('newtask').value = "";
}

function editRow(rowNumber) {
    document.getElementById(rowNumber).getElementsByClassName("taskcolumn")[0].innerHTML = "edited";
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