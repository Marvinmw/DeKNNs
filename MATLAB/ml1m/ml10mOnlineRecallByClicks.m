clear all
filename = '../ml10m/online/ml10mItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml10m/online/ml10mKemansuserdistribution.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../ml10m/online/ml10mHyruserdistribution.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
D=[A(1:15,3)';B(1:15,3)';C(1:15,3)']';
figure
bar(D);
axis([0 inf 0.5 1])
grid on;  
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600','600-640','640-680','680-720','720-760','>760'});
legend('Item-ml10m','Kmeans-ml10m','HyRec-ml10m','Location','northwest');
xlabel('the Number of Clicks');
ylabel('KNN-Recall')
title('KNN-Recall');

