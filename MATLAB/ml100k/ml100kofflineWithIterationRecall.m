figure
filename = '../ml100k/offlineknn/ml100k-Recall with Iterations/ml100kKemansrecallWithIterations.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml100k/offlineknn/ml100k-Recall with Iterations/ml100kItemsrecallWithIterations.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../ml100k/offlineknn/ml100k-Recall with Iterations/ml100kHyrrecallWithIterations.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);

plot(A(:,1),A(:,2),'b--s',B(:,1),B(:,2),'r:o',C(:,1),C(:,2),'k-.^','MarkerSize',4);
legend('Kmeans','Items','HyRec','Location','southeast');
xlabel('Iteration');
ylabel('KNN-Recall');
title('KNN-Recall Offline');