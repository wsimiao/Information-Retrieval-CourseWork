<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" pageEncoding="gb2312"%>
<%@ page contentType="text/html;charset= gb2312"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<title>Etsy Search Results</title>
<style>
img{
	width: 105%;
}
</style>
</head>
<body>
<div class="container">
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">Etsy</a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <form class="navbar-form navbar-left" name="sub_form" role="search" action="Query" method ="post" onsubmit="return validateForm()">
        <div class="form-group">
          <input type="text" name="SearchInput" class="form-control" placeholder="Search">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
      </form>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
</div>
<div class="container" style="padding-bottom: 20px; color: grey;">
<h3>You are searching: <c:out value="${parameter.getValue()}" /></h3>

</div>
<div class = "container">

<h3>Please Try Another Keyword!</h3>

</div>


</body>
</html>

<script>
function validateForm() {
    var x = document.forms["sub_form"]["SearchInput"].value;
    x = x.trim();
    var nospecial=/[*|\":<>[\]{}`\\()';@&$%]/;
    if (nospecial.test(x) || x=="") {
        alert("ÇëÊäÈëºÏÀíµÄÖÐÎÄ£ºPlease input valid keys");
        return false;
    }
}
</script>