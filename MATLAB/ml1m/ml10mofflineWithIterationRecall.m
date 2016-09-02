figure
filename = '../ml10m/offline/ml10mKemansrecallWithIterations.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
filename = '../ml10m/offline/ml10mItemsrecallWithIterations.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
filename = '../ml10m/offline/ml10mHyrrecallWithIterations.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);

plot(A(:,1),A(:,2),'b--s',B(:,1),B(:,2),'r:o',C(:,1),C(:,2),'k-.^','MarkerSize',4);
legend('Kmeans-ml10m','Items-ml10m','HyRec-ml10m','Location','southeast');
xlabel('Iteration');
ylabel('KNN-Recall');
title('KNN-Recall Offline');