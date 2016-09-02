clear all
filename='../Ciao/ciaooffline/CiaoHyrrecallWithIterations.txt';
delimiterIn=' ';
A1=importdata(filename,delimiterIn);
filename='../Ciao/ciaooffline/CiaoItemsrecallWithIterations.txt';
delimiterIn=' ';
A2=importdata(filename,delimiterIn);
filename='../Ciao/ciaooffline/CiaoKmeansrecallWithIterations.txt';
delimiterIn=' ';
A3=importdata(filename,delimiterIn);



figure
grid on
plot(A1(:,1),A1(:,2),'k-.x',A2(:,1),A2(:,2),'r:o',A3(:,1),A3(:,2),'b--^');
legend('HyRec-Ciao','Item-Ciao','Kmeans-Ciao','Location','southeast');
xlabel('Time /sec');
ylabel('KNN-Recall');
title( 'KNN-Recall Offline');