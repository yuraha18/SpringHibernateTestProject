Hi. This file describe how sepup the program.
1. Install PostgreSQL on our computer. 
Set username: postgres
    password: yuraha1995 
2. Create database "botsCrew" there
You can change this parametres in file: /com/yuraha/botscrew/resourses/spring.xml
In first deploy program creates all tables itself. After this change this parameter:
 <prop key="hibernate.hbm2ddl.auto">create</prop>
on this
 <prop key="hibernate.hbm2ddl.auto">validate</prop>
in the same file

Goodbye :) 