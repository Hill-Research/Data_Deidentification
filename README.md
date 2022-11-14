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

// standardTime will be null if the time string is infeasible.
```

### Iterface for Mask Algorithm on Address
In **src/** directory, run the following command:
```
javac Demo/Demo_MaskLocation.java;
java Demo/Demo_MaskLocation;
```
Explanation for Mask Algorithm on Address:
```
import Mask.MaskLocation;

String Location = "上海市闵行区东川路"// "山东省聊城市临清市"
String level = "provience"//"city"//"country";

MaskLocation.setLocationsName(); // Without this step, all the result will be null string.
String standardLocation = MaskLocation.mask(Location, level); // return the most possible result.
String standardLocation = MaskLocation.mask(Location); // similar to MaskLocation.mask, the default value of level is "provience".

// standardLocation will provide the maximum length of feasible substring even though the input string is infeasible.
```
