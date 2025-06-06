# Exam
Од владата на РМ ве ангажираат за синхронизација на процесот на државен испит, каде професорите ќе го изведуваат 
испитот во една просторија. Испитот се одржува во повеќе термини, каде во секој термин мора да има присутно еден 
професор и 50 студенти. По завршувањето на терминот, од просторијата прво излегуваат студентите и професорот, 
а потоа влегува нов професор и нови 50 студенти.

Притоа важат следните услови:

Во просторијата може да има само еден професор и точно 50 студенти.
Студентите не смеат да влезат ако во просторијата нема професор
Студентите не смеат да излезат додека професорот не каже дека испитот завршил
Професорот не може да излезе додека има студенти во просторијата
Просторијата иницијално е празна
Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирани се класите Teacher и Student, кои го симболизираат однесувањето на професорите и 
студентите, соодветно. Има повеќе инстанци од двете класи кај кои методот execute() се повикува само еднаш.

Во имплементацијата, можете да ги користите следните методи од веќе дефинираната променлива state:

state.teacherEnter()
Означува дека професорот влегува во училницата.
Се повикува од сите професори.
Доколку училницата не е празна во моментот на повикувањето, ќе се јави исклучок.

state.studentEnter()
Означува дека студентот влегува во училницата.
Се повикува од сите студенти.
Доколку нема професор во училницата (претходно не е повикан state.teacherEnter()), или има повеќе од 50 студенти внатре, 
ќе се јави исклучок.
Доколку студентите не влегуваат паралелно (повеќе истовремено), ќе јави исклучок.

state.distributeTests()
Го симболизира делењето на тестови на студентите и започнувањето на испитот.
Се повикува од сите професори по влегувањето на сите 50 студенти.
Доколку нема 50 присутни студенти во училницата, ќе се јави исклучок.

state.examEnd()
Го симболизира истекувањето на времето за испитот.
Се повикува од сите професори.
Доколку претходно не е повикан state.distributeTests(), ќе јави исклучок.

state.studentLeave()
Го симболизира излегувањето на студентот од училницата.
Се повикува од сите студенти.
Доколку се повика пред state.examEnd(), или ако претходно излегол професорот, ќе се јави исклучок.

state.teacherLeave()
Го симболизира излегувањето на професорот од училницата.
Се повикува од сите професори.
Доколку методот се повика, а сеуште има студенти во училницата, ќе добиете порака за грешка.

За решавање на задачата, преземете го проектот со клик на копчето Starter file, отпакувајте го и отворете го со 
Eclipse или Netbeans.

Претходно назначените методи служат за проверка на точноста на сценариото и не смеат да бидат променети и мораат да 
бидат повикани.

Вашата задача е да го имплементирате методот execute() од класите Teacher и Student, кои се наоѓаат во датотеката 
ExamSynchronization.java. При решавањето можете да користите семафори и монитори по ваша желба и нивната иницијализација 
треба да ја направите во init() методот.

При стартувањето на класата, сценариото ќе се повика 10 пати, со креирање на голем број инстанци од класата Teacher и 
една инстанца од класата Student, кај кои паралелно само еднаш ќе се повика нивниот execute() метод.

Напомена: Поради конкурентниот пристап за логирањето, можно е некои од пораките да не се на позицијата каде што треба да се.
Токму затоа, овие пораки користете ги само како информација.