<%@ page language="java" pageEncoding="gb2312"%>
<%@ page contentType="text/html;charset= gb2312"%>
<!DOCTYPE html >
<html>
<head>
	<link href="./css/bootstrap.css" rel="stylesheet" type="text/css" />
<meta http-equiv="Content-Type" content="text/html charset=UTF-8">
<title>Etsy Search</title>
<style>
body{
	background-image: url("./images/etsy.jpg");
	background-size: 100%;
}
.etsy_form{
	margin-top:120px;
	margin-left:50px;
}
.no-js #loader { display: none;  }
.js #loader { display: block; position: absolute; left: 100px; top: 0; }
.se-pre-con {
	position: fixed;
	left: 0px;
	top: 0px;
	width: 100%;
	height: 100%;
	z-index: 9999;
	background: url("./images/loader-64x/Preloader_2.gif") center no-repeat #fff;
}

</style>

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js"></script>
<script src="http://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.js"></script>
<script>
	//paste this code under head tag or in a seperate js file.
	// Wait for window load
	$(window).load(function() {
		// Animate loader off screen
		$(".se-pre-con").fadeOut("slow");;
	});
</script>

</head>
<body>
<div style="color: white; height: 20px;" class="container">
<h2>Etsy Enhanced Search Engine</h2>
</div>
<div class="etsy_form">
<form class="navbar-form navbar-left"name="sub_form" role="search" action="Query" method ="post" onsubmit="return validateForm()">
	<div class="form-group">
		<input type="text" name="SearchInput" class="form-control col-md-6" placeholder="Search">
	</div>
	<button type="submit" class="btn btn-default">Submit</button>
</form>
</div>
</body>
</html>
<script>
function validateForm() {
    var x = document.forms["sub_form"]["SearchInput"].value;
    x = x.trim();
    var nospecial=/[*|\":<>[\]{}`\\()';@&$%]/;
    if (nospecial.test(x) || x=="") {
        alert("请输入合理的中文：Please input valid keys");
        return false;
    }
}
</script>