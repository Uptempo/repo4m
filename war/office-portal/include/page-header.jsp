<!-- BEGIN HEADER -->
<div id="header" class="navbar navbar-inverse navbar-fixed-top">
   <!-- BEGIN TOP NAVIGATION BAR -->
   <div class="navbar-inner">
       <div class="container-fluid">
           <!-- BEGIN LOGO -->
           <a class="brand" href="index.html">
               <img src="/office-portal/img/logo.png" alt="Admin Lab" />
           </a>
           <!-- END LOGO -->
           <!-- BEGIN RESPONSIVE MENU TOGGLER -->
           <!--
           <a class="btn btn-navbar collapsed" id="main_menu_trigger" data-toggle="collapse" data-target=".nav-collapse">
               <span class="icon-bar"></span>
               <span class="icon-bar"></span>
               <span class="icon-bar"></span>
               <span class="arrow"></span>
           </a>
           -->
           <!-- END RESPONSIVE MENU TOGGLER -->
           <div id="top_menu" class="nav notify-row">
               <!-- BEGIN NOTIFICATION -->
               <ul class="nav top-menu">
                
               </ul>
           </div>
           <!-- END  NOTIFICATION -->
           <div class="top-nav ">
               <ul class="nav pull-right top-menu" >
                   <!-- BEGIN USER LOGIN DROPDOWN -->
                   <li class="dropdown">
                       <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                           <img src="/office-portal/img/avatar1_small.jpg" alt="">
                           <span class="username header-user-info"> Logged in as: <%=request.getAttribute("user-name") %></span>
                           <b class="caret"></b>
                       </a>
                       <ul class="dropdown-menu">
                           <li><a href="#"><i class="icon-user"></i> My Profile</a></li>
                           <li><a href="#"><i class="icon-tasks"></i> My Tasks</a></li>
                           <li><a href="#"><i class="icon-calendar"></i> Calendar</a></li>
                           <li class="divider"></li>
                           <li><a href="login.html"><i class="icon-key"></i> Log Out</a></li>
                       </ul>
                   </li>
                   <!-- END USER LOGIN DROPDOWN -->
               </ul>
               <!-- END TOP NAVIGATION MENU -->
           </div>
       </div>
   </div>
   <!-- END TOP NAVIGATION BAR -->
</div>
<!-- END HEADER -->