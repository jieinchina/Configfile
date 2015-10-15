***************************
* Hogan Configfile README *
***************************

This document is basically a catch-all for general information about the Property
File handle project(Configfile).  It is anticipated that this document will be 
updated from time to time with new information.

As a developer, we all need property to per-define some variables and we can use 
it at the runtime. after some experiences in java, i get some idea to make it be
smart like below, the standard file that show all features of this project.
##################################################################################
#hello, everyone
#this is a test configure file 
#that show you all features

[Default]
Name : Hogan ;测试
From=China

[Test]
Empty =  ;nothing
Ref = %(name)% Hu ;nested variable test
Title= Coder
What= Hi, [%(empty)%%(Ref)%] , %(test)% ;comments
     Great job! ;another test for comments
Test = i like the project. #this for comments test
Enable = ON  
##################################################################################
and the way to use like below java class:
##################################################################################
public static void main(String[] args) {
	ConfigFile._IMP().loadCFG("/tmp/test.ini");
	System.out.println(ConfigFile._IMP().getString("Test", "What"));
	System.out.println("empty float :" + ConfigFile._IMP().getFloat("Test", "Empty"));
	System.out.println("empty bool :" + ConfigFile._IMP().getBool("Test", "Empty"));
	System.out.println("bool-marked Enable :" + ConfigFile._IMP().getBool("Test", "Enable"));
}
##################################################################################
and the result like below:
##################################################################################
Hi, [Hogan Hu] , i like the project.
Great job!
empty float :0.0
empty bool :false
bool-marked Enable :true
##################################################################################
I think it's clear. and if you any concern, please let me know. and you can contact
me with below infos:
QQ   : 82425753
email: jie.hu.china@gmail.com

Hope you like it and helps!
:)