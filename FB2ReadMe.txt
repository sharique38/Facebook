name:Prateek Joshi     email:prateekjoshi2013@ufl.edu|
name: Sharique Hussain email:sharique88@ufl.edu		 | 	
---------------------------------------------------------

What is working ???
1)We made an encrypted version of the facebook project implemented in part 1
2)We implemented Authentication in the following way:
The client sends a hello message to te server , the server returns a nonce(secureRandom number) in return to the client.
The client then encrypts this nonce using its private key (Digital Signature), the server authenticates this signature by decrypting it using the public key of 
the client stored at the server.
3)Encryption:
We generate public and private key pairs for each user as well as AES keys for each user and encrypt this AES key using the public key of the client
 and store them at the server.
When we do a query like post we will request the encrypted AES key from the server and decrypt it at the client by the private key of that user.
And then use this decrypted key to encrypt or decrypt the data before storing or printing the result.



4)we have used all the four restAPIs i.e get ,put ,post and delete wherever suitable.
5)While using Post and Put APIs we have made sure to put the objects to be transferred in the payload 
rather than passing it as a string to prevent security risk.
6)We deserialize and serialize this payload on the server and client as required. 

Largest network we could manage ???
Largest network we successfully ran was with 1500 client actors.

Statistics found on web are:
http://www.socialbakers.com/statistics/facebook/
https://zephoria.com/top-15-valuable-facebook-statistics/

users - 1.49 billion monthly active, 936 million daily, means 10833 per sec
4.5 billion likes daily , approx 35000 likes per second
5 new profiles every sec
every 60 sec 136,000 photos are uploaded, 2266 per sec

when we scale to a level of 100000 users we need
0.15 photos per second
3 likes per second

Report:
We have created the 15 sec interval reports of the number of queries executed in the last 15 sec and stored that in output.txt file.Like this

GetProfile Queries->1144
GetProfilePosts Queries->1575
AddFriend Queries->1173
GetFriendlist Queries->1144
PostOnProfile Queries->2297
PostOnPage Queries->1913
AddAlbum Queries->750
AddPicture Queries->582
GetAlbum Queries->525
UnFriend Queries->150
GetPicture Queries->2470
CreatePage Queries->1571
LikePage Queries->841
UnLikePage Queries->150
-------------------------------------------------------------
-------------------------------------------------------------
Total no. of number of queries executed = 961 in last 15 seconds
-------------------------------------------------------------

 
Implementation Details
Project: Project4 Part2
1) As explained during lectures, this project implements Facebook API. We have created a backend server, an http server which binds this backend server and listens on a particular port. Http server contains implmentation of routing rest apis using spray can. And then there is a client which is a simulator, simulating users. All three entities takes command line argument as the number of users. 
2) Backend server contains a social network which is accessed by the multiple actors each represented as a user handle to access it's data in backend. The social network is implemented using multiple classes of user, profile, album, page, post & picture. User class is the main class containing all information about a user and is accessed by that users handle only.
3) The database for this implementation is initialized in memory like an object store.We have created different classes representing the different entities in facebook graph.
3) Client and backend communication happens through fbhttpserver using Json rest apis. And everything is transefferd after serializing and deserializing objects. fbhttpserver implements fbappiresolver which is basically doing the routing to the backend or to the client.
4) The client simulator contains a number of users taken from command line. These users are basically actors and different behaviours from each others, based on an analysis done by studying original facebook user behaviors on web. Source of information is shared in references
5) We have divided users into categories of their activity - aggressive, very active, active, passive, inactive. Each having different set of queries, with different level of activity.
6) We have used futures both during routing on httpserver and while sending query at client simulator.
7) For sending pictures we have used base64 encoding.
8) We have increased the security of the application using end to end encryption. 
---------------------------------------------------------------------------------------


Instructions for running the code
__________________________________________________________________

1)Execute sbt run command after going into the Facebook folder.
2)select the FacebookSimulator program first and enter the number of clients
3)Open another terminal and follow the same procedure and select fbHttpServer program and enter the number of clients
4)Open another terminal and follow the same procedure and select FacebookClientSimulator  program and enter the number of clients
5)The number of clients must be same  for all the three programs
_________________________________________________________________________
