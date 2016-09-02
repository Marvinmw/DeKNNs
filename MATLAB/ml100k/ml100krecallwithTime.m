clear all
filename='../ml100k/offlineknn/ML100kWithTime/ml100kHyrrecallWithTime.txt';
delimiterIn=' ';
A1=importdata(filename,delimiterIn);
%A1=A1(1:135,2)';
filename='../ml100k/offlineknn/ML100kWithTime/ml100kItemsrecallWithTime.txt';
delimiterIn=' ';
A2=importdata(filename,delimiterIn);
%A2=A2(1:135,2)';
filename='../ml100k/offlineknn/ML100kWithTime/ml100kKemansrecallWithTime.txt';
delimiterIn=' ';
A3=importdata(filename,delimiterIn);
%A3=A3(1:135,2)';

x=(1:0.5:68);
figure
grid on
hold on
plot(A1(:,1),A1(:,2),'k-',A2(:,1),A2(:,2),'r-.',A3(:,1),A3(:,2),'b--');
x1=1:20:181;
x2=1:20:101;
x3=1:20:121;
plot(A1(x1,1),A1(x1,2),'k^',A2(x2,1),A2(x2,2),'rs',A3(x3,1),A3(x3,2),'bh');
legend('HyRec-Ml100k','Item-Ml100k','Kmeans-Ml100k','Location','southeast');
xlabel('Time /sec');
ylabel('KNN-Recall');
title('ML100K-Offline KNN-Recall');