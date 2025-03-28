The APITest.ttl file has base structure for some of the manual tests in this pacage.
To set up: create a model called APITest (default namespace) and then
a task called "KRTTest".
After creating these, create a review in KRTTest. Use defaults.
Finally set properties in semaphore5.properties to point the tests to your
model and task. Specify the task model URI if you plan to run the KRT tests.
Make sure to delete test schemes after running each test.
