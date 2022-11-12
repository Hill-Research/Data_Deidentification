# Data_Deidentification

### How to compile?
In **src/** directory, run the following command:
```
javac Demo.java;
java Demo;
```

### Iterface for Blur Algorithm on Time
In **src/** directory, run the following command:
```
javac Demo/Demo_BlurTime.java;
java Demo/Demo_BlurTime;
```
Explanation for Blur Algorithm on Time:
```
import Blur.BlurTime;

String Time = "20200102"// "2020-01-02"// "2020年1月1日"
String level = "year"//"month"//"day";
String standardTime = BlurTime.blur(Time, level); // return the most possible result.
Vector<String> standardTimes = BlurTime.blurs(Time, level); // return all results.

String standardTime = BlurTime.blur(Time); // similar to BlurTime.blur, the default value of level is "year".
Vector<String> standardTimes = BlurTime.blurs(Time); // similar to BlurTime.blurs, the default value of level is "year".

// standardTime will be null and standardTimes.size() with be 0 if the time string is infeasible.
```
