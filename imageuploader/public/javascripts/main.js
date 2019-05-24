

if (window.console) {
  console.log("Welcome to aui dashboard");
}


function getPreference(){
    var prefGet = $.ajax({
      url: "getPref",
      method: "GET",
      success: function( result ) {
        // $( "#preference-get" ).html( result.result || "Empty Preference" );
        alert(result.result || "No Preference Data");
      }
    });
}


function setPreference(){
    var prefPost = $.ajax({
      url: "setPref",
      method: "POST",
      data: {
        preferences: $("#preference-set").val()
      },
      success: function( result ) {
        alert("updated rows: " + result.result)
      },
      error: function( error ) {
        alert(error)
      }
    });
}

