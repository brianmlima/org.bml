###
# #%L
# org.bml
# %%
# Copyright (C) 2008 - 2013 Brian M. Lima
# %%
# This file is part of org.bml.
# 
# org.bml is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# org.bml is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
# #L%
###

TO_DIR="/opt/nokia/codebase/NewHere/here/src/main/java" ;
FROM_DIR="/opt/nokia/codebase/mrjobs-new";
for file in $(find ${FROM_DIR} -type f -regex '.*java$' | grep -v '/target/'); do 
 echo "Processing ${file}";
 #echo "Parsing package for determining destination directory" 
 DEST_DIR="$( awk -F' ' '/package com/  {gsub(/;/,"", $2);gsub(/\./,"\/", $2);gsub(/\./,"\n", $2); print }' ${file})";
echo ${DEST_DIR};
echo "${file}" "${DEST_DIR}/$(basename ${file})";



#mkdir -p "${DEST_DIR}" ;
#echo "${file} ${DEST_DIR}" ;
#cp "${file}" "${DEST_DIR}/$(basename ${file})";
unset DEST_DIR;
done;






 if [[ -n ${DEST_DIR} ]] ; then
  echo "SUCCESS: Parsed new destination directory ${DEST_DIR} from Java file at ${file}";
 else
  echo "FAILURE: Unable to parse package from Java file at ${file}. This file may be corrupt" ;
  echo "FAILURE: Failing Safe, See error log for details";
  break;
 fi 



TO_DIR="/opt/nokia/codebase/NewHere/here/src/main/java" ;
FROM_DIR="/opt/nokia/codebase/mrjobs-new";
for theFile in $(find ${FROM_DIR} -type f -regex '.*java$' | grep -v '/target/'); do 
 echo "Processing ${theFile}";
 echo "Parsing package for determining destination directory" 
  DEST_DIR=`cat ${theFile} | awk -F' ' '/package com/  {gsub(/;/,"");gsub(/\./,"\/");gsub(/\./,"\n"); print $2}'` ;
echo ${DEST_DIR};
echo "${theFile} -  ${DEST_DIR}/$(basename ${theFile})";
unset DEST_DIR;
done;





 awk -F' ' '/package com/  {gsub(/;/,"");gsub(/\./,"\/");gsub(/\./,"\n"); print $2}' /opt/nokia/codebase/mrjobs-new/util/Utility.java





TO_DIR="/opt/nokia/codebase/NewHere/here/src/main/java" ;
FROM_DIR="/opt/nokia/codebase/mrjobs-new";
for theFile in $(find ${FROM_DIR} -type f -regex '.*java$' | grep -v '/target/') ; do 
 COPY_FROM="${theFile}"
  cp ${COPY_FROM} ./tmpsrc;
done;



grep --no-filename -o -E 'package (com[^;]*)' * | sort | uniq

gsub(/;/,"");gsub(/\./,"\/");gsub(/\./,"\n");

awk -F' ' '/package com/ { gsub(/;/,"",$2);gsub(/\./,"\/",$2);gsub(/\./,"/",$2);gsub(/[ \t\r\n]+/, "",$2); print $2}' * | sort | uniq | xargs -J % mkdir -p % 


awk -F' ' '/package com/ { gsub(/;/,"",$2);gsub(/\./,"\/",$2);gsub(/\./,"/",$2);gsub(/[ \t\r\n]+/, "",$2); print "cp "FILENAME" "$2" ;"}' *.java > DoCopy.sh




cat FeedColumn.java | sed -e 's/[ ]+\/[*]{1,2}//g'


cat FeedColumn.java | grep -v '  /**'


sed -i -e 's/BML:NOTE:/<b>BML:NOTE:<\/b>/g'  FeedColumn.java



Geocoded to seconds.


Navteq backdoor to nokia

Plug into red jack

GOTO https://in.nokia.com this for some reason allows you to gain access to the network internal and external.

https://source.nokia.com

 

svn --username brilima checkout 'https://source.nokia.com/Local-Commerce-Engineering/svn/lce'


svn --username brilima cleanup



01915#casius!@#$

System/Library/Java/Extensions/


import shape file,
work out granualrity of mercador quadrangles
provide lookup for euclidian and hashed to quadrangle.


MONGO_FILE="mongodb-linux-x86_64-2.4.6.tgz" ;
MONGO_HTTP_LOC="http://fastdl.mongodb.org/linux" ;

wget "${MONGO_FILE}/${MONGO_HTTP_LOC}" ;

tar -xzvf "${MONGO_FILE}" ;








Notes on modifications to com.here.lcapi.quadcode.Geohash

Given that this class is the core of the QuadCode system I thought a good place 
to start was cleaning up the Geohash.java code. I usually do not go tho the 
extent of refactoring that I did on this class but given the dependance we have
on it now and in the future I took it upon myself to give it the attention I 
would a real time class less some saftey critical rules and error checking 
(throwing declared exceptions) that I will push in once I get a better idea of 
where this class is refrenced.

Added Dependancies
Commons Math 3 for FastMath
Commons Logging for impl agnostic Logging. We may want to take back this change
if users of this class can be forced to catch specific exceptions.

Resource optomizations

Made methods static. This should have no effect other than removing the need to 
instance the object which seemed superflous. IE: generally any method that does
not depend on an objects instance scope state should be static so the compiler 
can use inline.

Changed all usage of Math to apache commons FastMath which is known to be 
generally faster than the standard java implementation and is a drop in 
replacement. I did this by inheriting the static methods from FastMath to reduce 
code verbosity and allow for easy replacement should we ever want to use a 
platform specific Math or need to enforce constant time to under .5 ULP precision.




toRad deprecated and changed to use FastMath.toRadians this should be faster than doing the 
math manually.

Refactored small if else statements to faster switch statements. This is a good 
optomization to use in general when you can because optomizers will usually 
implement the switch as a branch table as opposed to a series of conditionals.


Removed toRad method and replaced with static import of FastMath.toRadians


Removed unused import and unnecessary import

import java.lang.Math ; 

I find it is best to use static imports for math methods, this allows you to use
the fastest implementation. This is especially handy when using methods like 
exp,cosh, and hypot where FastMath out performes java Math significantly. 

NOTE: Since 1.5 Math improved significantly so FastMath should be avoided less 
the methods mentioned above.


import java.awt.geom.Point2D ;

This was unused. Also we should stay away from using anything in the awt package
unless we are building native apps. There are much faster and lighter weight 
alternatives. I do like the idea of using Path2D.Double or at least an equivilant
abstraction so we are not relying on the definition and strict ordering of 
double[] but this needs to be debated as there are speed advantages to using an
assumed order as opposed to named methods in a wrapper class. 

Added private constant for representing the world as a box. This was basicly 
being done already so I built the constant and replaced refrences to the values
with Arrays.copyOf(THE_WORLD, THE_WORLD.length).

Refactored variable names to be more descriptive and denote scope as 
well as changed input params to final where possible.


Some Suggestions:

NOTE: This is for finished code. Personally I do not write code according 
to the following rules all the time. Most of the time I prototype, build
tests, and then make a refactor pass to enforce the following and other best 
practices. These are in no particular order they are just gleemed from some of
the refactoring and code reviewing I have been doing as of late.


1. It is poor form and non-deterministic to rely on the compiler to optomise 
your code. I can also attest to the fact that optimal code generally looks 
cleaner, is smaller, and easier to read. Help the compiler and it will help you!

2. Document all classes, public methods, and anything that you think a 
generalist programmer could use a hint. I know this sucks but if anyone else 
might ever touch your code a simple hint and the few seconds it takes to write 
it can save others hours of time. I like to think of it as interest on technical 
debt. Not documenting at all has a certain cost and as time goes on there is an 
interest rate on that debt. The less you document the higher the interest rate 
and we all hate interest rates, unless of course you are the lendor.

3. Never use a StringBuffer unless you need to be thread safe. StringBuilder 
is much faster drop in alternative and should be used unless you are only doing 
char concationation, then a CharBuffer is usually the best choice if you need 
to return -- or at some point convert to String. Also if you are using builders 
and or unbound buffers for a specific reason then consider using a pool.

4. Any array backed collection should be declared with a capicity. This really 
helps runtime memory alocation and as a result speed. Its easy to do, and if 
you know the number of elements in an array backed collection all you need to 
do is pass it to the constructor. Another related hint is to profile for and
declare a growth factor if your collections size is inherently unknown at 
declaration time. Also using unbound collections is a violation of real-time and 
safty critical standards as it almost always means your function is not going to 
run in a predictable timeframe and can possubly cause an out of memeory type of 
system level exception. Obviously it is not reasonable to require meeting these
standards completely outside of a real time -- safty critical system but comming
close can vastly improve the stability and efficiancy of the code which directly 
results in more uninterrupted vacation time!

5. If you see an if elseif statement with constants or literals it should be 
written as a switch statement. Switch is much faster and the default case allows 
for easily dealing with the edge case where none of your values match. Basicly 
if you see an ifelse that can be written as a switch it should be. The compiler 
will reward you.

6. Never, NEVER dealare a variable inside a loop! In java this results in a mass
of refrences bieing pushed to the young generation that need to be checked for 
scope and collected. It is much cleaner and faster to define your 'pointers' 
first and only reassign values as necessary. NOTE: {} does limit scope in java 
if you declare within them, some compilers can detect the case where you delare
and use once without modification so there are situations where this rule can be
broken and not incur any performance penalty.

7. I can not think of any reason why a method should ever return an implementation
class for a Collection. Im sure there are reasons but I can not think of any 
reason a calling class would need to know if a List<Double[]> is an 
ArrayList<Double[]>. This one is not so much an optomization hint as a way to 
decouple your implementation from functionality. IE: the Geohash class has 
several methods that return ArrayList<String>, I am not sure how pervasive use 
of these methods is so until I am sure I do not want to change the methods 
signature to return List<String>.

8. Dont be afraid to inherit static methods from classes that you may want to 
drom in and out. I almost never do this less the Math VS Commons Math and 
FastMath example. IE: You have to change the classes method code when changing 
the math implementation unless you inherit the static methods. Then you can 
change the impl by editing the import 'header'. 

The above should be used carefully as you do not want to intertwine code too 
much. There is a fine line between making the class readable/flexable and making
it brittle. Much like Mr. Cash.... walk the line. 

9. ArrayList<Double[]> and ArrayList<double[]> are the same declaration. 
Personally I do not like the auto boxing and unboxing of primiatives that Java 
has as it seems to me like just enough rope to hang yourself. Be wary of the 
cost of these operations as they are usually incurred in loops and if you know
you are going to incur the cost then explicitely declare it to help the 
compiler optomise for it. 

NOTE: There are issues with this which you can see in the
Geohash cover methods (try changing the declarations to Stack<Double[]>) as 
Java also does not universally box and unbox so declarations like 
ArrayList<double[]> are stil necessary or at least many compilers complain as 
they should if you try to pop off of Stack<Double[]> to a double[]. Anyway there
are better, or at least more deterministic, ways of dealing consistantly with 
this like using some of the fast collections built on commons primatives.










latLon2Quad This method should see heavy usage so I paid special attention to it.

Changed StringBuffer to CharBuffer as there is no concurrency issue, 
we know the maximum buffer size via the precision parameter,and its much faster 
than an unbound StringBuilder. NOTE: I did not use a charArray or char[] as the 
CharBuffer provides the necessary toString method.

Moved variable declarations to outside of loop and changed to for loop type where 
apropriate.

Replaced array comparisons and inits with Arrays calls where possible.

QUAD_CHARS
Changed from String array to char array. Will speed up StringBuilder appends.

isOverlapped
eliminated two boolean refrences








1. Are our latitudes geodetic or geocentric?




Dale Defort





ssh -i .ssh/hadoop.pem -o StrictHostKeyChecking=no -o ServerAliveInterval=30 root@ec2-23-21-8-14.compute-1.amazonaws.com



awk -F' ' '{print $0}' categories.xml



awk -F' ' '/find-places-icon-id/ {print $2}' categories.xml




create 'categories','cf', {NAME=>'business-key'}, {NAME=>'name'}, {NAME=>'find-places-icon-id'}




















