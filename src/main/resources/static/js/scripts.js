function logout() {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/logout", true);
    xhr.setRequestHeader(header, token);
    xhr.send();
    $("#user").html('');
    $(".unauthenticated").show();
    $(".authenticated").hide();
    return true;
}