# od-GCS
Repository for Open Drone Ground Control Station

The Open Drone Project is aimed at developing commercial grade drones that depend entirely on open-source software and hardware. This repository provides source code for the following:

a. The Ground Control Station. 

![alt tag](https://user-images.githubusercontent.com/1425839/33735392-3212d4ae-dba0-11e7-896e-5a22053e53fa.png)

The GCS is an interface between the drone operator and the drone. Software and harward artifacts provide the necessary interface and relay mechanisms for drone operators to excute command and control over their drone. This includes tasks like guidance, relay of real-time telemetry to mention but a few. This interface relies on 3D visualization using NASA World Wind Maps, and generated 3D terrain using Open Drone Map.

b. The Open Drone Data Visualization Portal is going to fork this project, and concentrate on data visualization and eliminate command and control features offered by the GCS. Additional capability will be:
  i. Visualization of historical aerial missions, simulations and flight plans.
  ii. Manipulation to filter out relevant data concerning particular sensor data, such as superimposing atmospheric data with terrain maps.
  iii. Analytics like volumetric analysis and related phototgrammetric capabilities including distance measures, object dimension calculation etc.

c. To install and use this toolkit, you require Java 1.4 or higher. You also require the NASA World Wind toolkit, which can be downloaded at: https://github.com/NASAWorldWind/WorldWindJava
  To invoke the toolkit, execute the run.bat or run.sh. There may be exceptions through that provide additional configuration information
  
Licensing Terms:
Work and material herein is released under a GNU General Public License. A copy of license terms can be found here: https://www.gnu.org/licenses/gpl.html
