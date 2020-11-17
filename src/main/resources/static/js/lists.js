function removeList(Id) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var result = false;
    $.ajax({
        type: "GET",
        url: "/listEmpty?listId="+Id,
        beforeSend: function(request) {
            request.setRequestHeader(header, token);
        },
        success: function(data){
            result = data.empty;
            if(!result){
                result = confirm($('#'+Id+' .listcolumn').html() + " contains tasks, are you sure you want to remove it?");
                if( !result ) { return; }
            }
            $.ajax({
                type: "POST",
                url: "/removeList",
                beforeSend: function(request) {
                    request.setRequestHeader(header, token);
                },
                contentType: 'application/json',
                data: JSON.stringify( {listId: Id} ),
                success: function(data){
                    window.location.reload();
                }
            });
        }
    });
}