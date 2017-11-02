<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
     import="com.mongodb.BasicDBObject"
     import="com.mongodb.DB"
     import="com.mongodb.DBCollection"
     import="com.mongodb.DBCursor"
     import="com.mongodb.MongoClient"
     import="java.net.UnknownHostException"
	 import="com.sun.org.apache.bcel.internal.generic.NEW"
	 import="com.mongodb.DBObject"
	 import="com.mongodb.Mongo"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style>

  #nav_wrapper{
 display:inline;
}
#nav_wrapper ul,li{
  display:inline-block;
  padding:15px;
  margin:5px;
  padding-top:5px;
  decoration:none;
  padding-top:10px;
  font-size:20px;
   
   }


}
hr{
    display:block;
	width:100%;
}
#nav_wrapper a{
   color:#2E9AFE;

}
#nav{
  display:inline;
}
hr{
     box-shadow: 10px 10px 5px #888888;

}

 /* logo purpos*/
a.glyphicon.glyphicon-book{
  
   padding:5px;
   margin:5px;
   font-size:30px;
   decoration:none;
   color:#2E9AFE;
}
p.quote-font.text-center{
    font-size:25px;
	color:#2E9AFE;
}
#smile-size{
   height:30px;
   width:30px;
}
footer {
  color:#FBFBEF;
}
#list-alignment li{
   
   display:block;
   height:20px;
        

}
.content-headings{
    margin-left:40px;
    color:#0080FF;
}
       
</style>
<title>Insert title here</title>
</head>
<body>
<hr>
<div class="container-fluid" class="row">

     <div class="col-sm-4"><a href="index.html" class="glyphicon glyphicon-book " class="quote-font">RoughBook</a></div>
	       <div class="col-sm-2"></div>
          <div class="col-sm-6" id="nav_wrapper" class="container-fluid" style="align:inline;">
                    <nav>
	                  <ul>
          
                           <li><button class="btn btn-link"><a href="index.html"><b>Home</b></a></button></li>
                           <li><button class="btn btn-link"><a href="#"><b>Courses</b></a></button></li>
                           <li><button class="btn btn-link"><a href="#"><b>Videos</b></a></button></li>
					       <li><button class="btn btn-link"><a href="editor.html"><b>Work Bench</b></a></button></li>
                           <!-- <li><button class="btn btn-link"><a href="about_us.html">About Us</a></button></li>-->
						   
	                   </ul>
	 	             </nav> 
	        </div>
  	


	</div>
<hr>
<div class="row">
<div class="col-sm-4" id="list-alignment">
     <h2 class="content-headings">Content</h2><hr> 
      <ul>   
           
            <li><a href="display.jsp">html introduction</a></li>
            <li><a href="html_editor.jsp">html editors</a></li>
            <li><a>html basics</a></li>
            <li><a>html elements</a></li>
            <li><a>html attributes</a></li>
            <li><a>html tags</a></li>
            <li><a>html comments</a></li>
            <li><a>html comments</a></li>
            <li><a>html colors</a></li>
            <li><a>html css</a></li>
            <li><a>html links</a></li>
            <li><a>html images</a></li>
            <li><a>html tables</a></li>
            <li><a>html list</a></li>
            <li><a>html block</a></li>
            <li><a>html classes</a></li>
            <li><a>html iframes</a></li>
            <li><a>html javascript</a></li>
            <li><a>html entities</a></li>
            <li><a href="editor.html">html code ground</a></li>
     </ul>           


   

</div>
<div class="col-sm-8">
<% 



	Mongo mg = new Mongo("Localhost", 27017);
    DB db = mg.getDB("testdoc");
	DBCollection collection = db.getCollection("testdoc");
	BasicDBObject doc = new BasicDBObject();
	DBCursor cursor = collection.find();
	int rec=1;
	
    try {
       while(cursor.hasNext()) 
       {
    	   DBObject str= cursor.next();
    	  
    	   //out.println("Record:"+rec++);%>
    	   <br><% 
    	   out.println("\n");out.println('\n');
    	   out.println(str.get("head tag"));%>
    	    <br><% 
    	   out.println();out.println('\n');
    	   out.println(str.get("html-question-bank"));%>
   	    <br><% 
    	  
       }
    	} 
    finally 
    {
       cursor.close();
    }
    

mg.close();

%>
</div>
</div>
</body>
</html>