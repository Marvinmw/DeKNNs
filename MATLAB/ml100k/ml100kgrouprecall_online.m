figure
filename = '../ml100k/onlineknn/ml100kItemsuserdistribution.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml100k/onlineknn/ml100kKemansuserdistribution.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../ml100k/onlineknn/ml100kHyruserdistribution.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
D=[A(:,3)';B(:,3)';C(:,3)']';
bar(D);
axis([0 inf 0.6 1.01])
grid on;  
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600'});
legend('Item','Kmeans','HyRec','Location','northwest');
xlabel('the Number of Clicks');
ylabel('KNN-Recall')
title('KNN-Recall Online');