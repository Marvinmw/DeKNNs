figure
filename = './offlineknn/ml100Itemskdistributionpredicate.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
plot(A(:,1),A(:,2),'-*');
legend('ml100k');
xlabel('Group');
ylabel('RMSE')
title('RMSE of Each Group in Test Dataset');

figure
filename = './offlineknn/ml100kItemsrecallWithIterations.txt';
delimiterIn = ' ';
B = importdata(filename,delimiterIn);
plot(B(:,1),B(:,2),'r-o');
legend('ml100k');
xlabel('Iteration');
ylabel('Recall')
title('Recall with the Iteration');


figure
filename = './offlineknn/ml100kItemsuserdistribution.txt';
delimiterIn = ' ';
C = importdata(filename,delimiterIn);
plot(C(:,1),C(:,3),'g-+');
legend('ml100k');
xlabel('Group');
ylabel('Recall')
title('Recall of each Group');


figure
filename = './offlineknn/ml100kItemsuserdistribution.txt';
delimiterIn = ' ';
D = importdata(filename,delimiterIn);
bar(D(:,1),D(:,2));
xlabel('Group');
ylabel('Number')
title('User Distribution');

figure
filename = './offlineknn/ml100kItemsml100kuserprofile.txt';
delimiterIn = ' ';
E = importdata(filename,delimiterIn);
hist(E(:,1),);
xlabel('Group');
ylabel('Number')
title('User Distribution');
