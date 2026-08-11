class Solution {
    public int missingNumber(int[] nums) {
     int n = nums.length;
     int exsum = n*(n+1)/2;
     int actualSum =0;
     for(int num : nums)
     actualSum +=num; 
        
     return exsum - actualSum;   
    }
}