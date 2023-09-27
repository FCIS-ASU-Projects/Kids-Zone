# Kids-Zone

Parents' android mobile app helps in controlling children’s mobile use behavior, by using the mobile's front camera to take photo of the user, then classify the user's age from its face and if the user is a child, the app will Block specific apps, Freeze the mobile after child’s daily screen time is finished.

### **Process of Age Classification:**

- **Preprocessing**

  **First,** we apply face and landmark detection using [yolov5-face](https://github.com/deepcam-cn/yolov5-face), the model was trained on a WiderFace dataset, and achieved an accuracy of 92.75%.

  **Then,** filtering detected faces using IOD (Inter-Ocular Distance) metric to take the closest                                     face to the camera.

  **After that,** we apply face alignment.

  **Finally,** we do normalization and resize the image to be 3x96x96.

- **CNN Classification model**

  We used different CNN models which are Shufflenetv2, Mobilenetv2, and VGG-Face. At   the end we use **MobileNetv2**, as it’s lightweight and has comparable results with other related works.
  
  The Datasets we used are [Adience](https://www.kaggle.com/datasets/alfredhhw/adiencegender) , [UTK](https://susanqq.github.io/UTKFace/),  and private dataset (collected by us to increase the number of     children images) datasets. 
  
  The classes are (0 - 3), (4 - 6), (7 - 14), (15 - 20), (21 - 32), (33 - 45), (46+). We applied down sampling to majority classes and cost-sensitive technique beside down sampling trying to overcome issue   of unbalanced classes.

  Then we split data per class: Train: 70%, Validation: 15%, Test: 15%. 

  Trained with Adam optimizer with learning rate 0.0001, batch size 32, with early stopping, dropout with rate 0.35 to avoid overfitting and make training of network more stable, and Loss function is Categorical Cross entropy and achieved an Exact accuracy 51%. 




- **Results**

  |***Class***|***Accuracy*** |*1-off Accuracy*|
  | :- | :- | :- |
  |***0-3***|81%|94%|
  |***4-6***|52%|69%|
  |***7-14***|42%|59%|
  |***15-20***|43%|63%|
  |***21-32***|38%|62%|
  |***33-45***|34%|55%|
  |***46+***|65%|83%|
  |***Overall***|51%|70%|






### **User Manual:**

**Welcoming Pages:**

   The first-time opening Kids Zone, these interfaces are displayed to give you a brief knowledge about this app and to confirm the privacy policy.
  
  ![image](https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/ecf34d53-0693-419f-9844-a476cbaf3383)



**Permissions:**

  After the privacy policy agreement, you should give “Appear on top” permission to the app to be able to work in your mobile background.
  
  
  ![image](https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/b81c5f0d-5830-4f9c-a81c-664fb4bf7326)


 <img align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/c99441b8-7877-4f48-9e46-1ff56d199783" />
 <p>You can give camera permission each single time you use the app, or you can give this permission along.</br>N.B.
    Kids Zone app cannot work without these 2 permissions (Appear on top & Camera)</p>




</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>


**Home Page:**

 <img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/08c3b5b8-2b59-4c6d-b3a9-43ce614da56f"  />
 <img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/0d041913-09e1-4ff1-9770-19062f09b19e"  />
 <p>From this moment, you can <br clear="left"/>
    start or stop the app whenever <br clear="left"/>
 you want from this switch <br clear="left"/>
button. <br clear="left"/>
When you switch this button <br clear="left"/>
on, the camera starts <br clear="left"/>
monitoring the mobile user by <br clear="left"/>
 taking pictures every 20 <br clear="left"/>
seconds (without saving these <br clear="left"/> 
pictures) and detects the user <br clear="left"/>
 age, if the user is within the <br clear="left"/>
range that was specified in Kids <br clear="left"/>
 Zone, then mobile apps are <br clear="left"/>
blocked, and time screen <br clear="left"/>
countdown starts counting. </p>

</br></br></br></br></br></br></br></br></br></br>

**About Page:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/41d63f4f-eefa-43b6-b1f8-299ab77b0355" />
<p>If you press on the (i) button which is on the top rightmost of the previous interface,
    you can read more about Kids Zone.</p>

</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

**Unblocked Apps Page:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/9ecde956-b72f-4289-8697-b0fe3a33aaa0" />
<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/82e8befa-6739-4d35-9b16-18e9ab6c992b" />
<p>By pressing the “Unblocked Apps” button, which is on the home page, the list of apps that are unblocked when the kid uses the mobile is displayed. You can block and unblock any app you want. By default, all mobile apps are blocked, and you can unblock any of them.</p>

</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

**All Apps Page:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/eca6c016-81f9-4966-8495-6df14e5e3064" />
<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/094ae98e-4b05-451a-85b7-dc7fdef3b0e6" />
<p>When you press on the “All Apps” button downmost the interface, all mobile apps are displayed in this interface and by pressing on any one of them, you can unblock it. </p><p></p><p>Example, unblock NinetySanya and Call.</p>

</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

**Screen Time Page:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/93eef4e8-2aa1-4bfa-9006-e7e748d845db" />
<p>By pressing the “Screen Time” button, which is on the home page, you can choose the daily time duration of using the mobile for kids (by default it is 30 minutes). After this duration, the mobile is totally frozen and will be unfrozen when the camera captures a picture of a user outside the age range which apps are blocked for.</p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p>



</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

**Select Age:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/77577521-bbf3-4267-8ea5-cb3037f43aed" />
<p>By pressing the “Select Age” button, which is on the home page, you can choose the age range that Kids Zone will work for. </p><p>Examples: “-6” means from 0 to 6, “-9” means from 0 to 9, etc.…</p>


</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

**Opening Blocked App:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/9ff95bc5-3051-42fc-bc14-540e851b857e" />
<p>When a kid holds the mobile and the camera detects that, if the kid tries to open a blocked application, this screen appears.</p>

</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

**Mobile Freeze:**

<img  align="right" src="https://github.com/FCIS-ASU-Projects/Kids-Zone/assets/66036896/e0698cec-26bf-476a-b6cb-17258e8cf880" />
<p>When screen time for kids is finished, this screen will be displayed on the whole mobile screen and can’t be removed till the camera detects that the user is not a kid.</p>


</br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br></br>

### **Note:**

For more info about our app click [here](https://drive.google.com/file/d/1pP2k5PahdbBpF3UlN7JyVOXhgLAVVcLz/view?usp=sharing).




