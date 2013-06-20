TextBin
=======

TextBin is a service that acts as a light-weight and secure way to make data accessible from
multiple machines or programs. By using this API that communicates with TextBin, you can send
data onto TextBin that can later be retrieved using the API provided that you have the password.
The encrypted data in the bin can, in a way, be seen by everyone, just like how anyone can access a deposit
box; however, in order to unlock the deposit box or view the data in its decrypted form, you need
a key.

Java
----

The Java API allows users to push data into a "bin" and pull data from that "bin". Provided that
the user has the required encryption key and the "address" of the bin, the user can access the
data inside that bin.

~~~Java
// The bin location is an assigned 6 character, alphanumeric string
String binLocation = "AAAAAA";

// The key used to encrypt the data when pushed
String key = "my_key";

// Data to push into the bin
String text = "Hello World!";

TextBin.pushAndEncryptString(key, binLocation, text);
~~~

In order to retrieve the data, the user simply needs to point to the bin and decrypt the data
with the correct key

~~~Java
// The bin location is an assigned 6 character, alphanumeric string
String binLocation = "AAAAAA";

// The key used to encrypt the data when pushed
String key = "my_key";

System.out.println(TextBin.getAndDecryptString(key, binLocation));
~~~
