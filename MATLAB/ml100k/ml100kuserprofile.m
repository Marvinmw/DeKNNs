clear all
figure
filename = '../ml100k/ml100kItemsuserprofile.txt';
delimiterIn = ' ';
A = importdata(filename,delimiterIn);
nbins = 10;
[n,y]=hist(A(:,1),nbins);
% h = histogram(A(:,2),10);
bar(n,0.3);
grid on
set(gca, 'xticklabel',{'0-60','60-120','120-180','180-240','240-300','300-360','360-420','420-480','480-540','540-600'});
title('User Distribution on Ml100k');
xlabel('the Number of Clicks');
ylabel('the Number of Users');