clear all
filename='../ml10m/offline/ml10mHyrrecallWithTime.txt';
delimiterIn=' ';
A1=importdata(filename,delimiterIn);
%A1=A1(1:135,2)';
filename='../ml10m/offline/ml10mItemsrecallWithTime.txt';
delimiterIn=' ';
A2=importdata(filename,delimiterIn);
%A2=A2(1:135,2)';
filename='../ml10m/offline/ml10mKemansrecallWithTime.txt';
delimiterIn=' ';
A3=importdata(filename,delimiterIn);
%A3=A3(1:135,2)';


figure
grid on
hold on
plot(A1(:,1),A1(:,2),'k-',A2(:,1),A2(:,2),'r-.',A3(:,1),A3(:,2),'b--');
x1=1:20:540;
x2=1:20:360;
x3=1:20:400;
plot(A1(x1,1),A1(x1,2),'k^',A2(x2,1),A2(x2,2),'rs',A3(x3,1),A3(x3,2),'bh');
legend('HyRec-Ml10','Item-Ml10m','Kmeans-Ml10m','Location','southeast');
xlabel('Time /sec');
ylabel('KNN-Recall');
title(' KNN-Recall Offline');