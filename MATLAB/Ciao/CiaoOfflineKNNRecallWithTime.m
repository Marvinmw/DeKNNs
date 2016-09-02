clear all
filename='../Ciao/ciaooffline/CiaoHyrrecallWithTime.txt';
delimiterIn=' ';
A1=importdata(filename,delimiterIn);
filename='../Ciao/ciaooffline/CiaoItemsrecallWithTime.txt';
delimiterIn=' ';
A2=importdata(filename,delimiterIn);
filename='../Ciao/ciaooffline/CiaoKmeansrecallWithTime.txt';
delimiterIn=' ';
A3=importdata(filename,delimiterIn);



figure
grid on
hold on
x1=1:10:160;
x2=1:10:70;
x3=1:10:70;
plot(A1(:,1),A1(:,2),'k--',A2(:,1),A2(:,2),'r-.',A3(:,1),A3(:,2),'m-');
plot(A1(x1,1),A1(x1,2),'ks',A2(x2,1),A2(x2,2),'r<',A3(x3,1),A3(x3,2),'mp');
legend('HyRec-Ciao','Item-Ciao','Kmeans-Ciao','Location','southeast');
xlabel('Time /sec');
ylabel('KNN-Recall');
title('KNN-Recall Offline ');