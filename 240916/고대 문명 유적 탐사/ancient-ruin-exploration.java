import java.util.*;
import java.io.*;

public class Main{
    static StringBuilder sb = new StringBuilder();
    static int K,M;
    static int[][] arr;
    static List<Integer> scoreList;
    static Queue<Integer> spare;
    static boolean[][] visit;
    static int maxRoundScore;
    static List<int[]> list,temp;
    final static int SIZE =5;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String [] input = br.readLine().split(" ");
        K = stoi(input[0]);
        M = stoi(input[1]);
        arr = new int[SIZE][SIZE];
        spare = new ArrayDeque<>();
        for(int i =0; i<SIZE; i++){
            input = br.readLine().split(" ");
            for(int j =0; j<SIZE; j++){
                arr[i][j] = stoi(input[j]);
            }
        }
        input = br.readLine().split(" ");
        for(int i =0; i<M; i++){
            spare.add(stoi(input[i]));
        }

        simulation();
        for (int value : scoreList)
        sb.append(value + " ");
        sb.append("\n");

        System.out.println(sb);

    }
    public static void simulation(){
        scoreList = new ArrayList<>();
        for(int t =0; t<K; t++){
            maxRoundScore =0;
            list = new ArrayList<>(); //이번 라운드에서 점수 획득할 수 있는 좌표 저장
            temp = new ArrayList<>();
            //가장 점수를 많이 획득할 수 있는 지점 구하기
            int[] expect = getExpectPosition();

            //점수 획득할 수 없으면 종료
            if(list.size() ==0) return;

            //rotate(x,y,degree) 특정 좌표를 기준으로 구한 각도로 회전
            arr = rotate(expect[0], expect[1], expect[2]);

            // 지우기
            for (int[] pos : list)
                removeItem(pos[0], pos[1]);

            // 채우기
            fillItem();

            // 추가 점수 획득
            while (true) {
                visit = new boolean[SIZE][SIZE];
                int count = 0;
                temp.clear();
                for (int i = 0; i < SIZE; ++i) {
                    for (int j = 0; j < SIZE; ++j) {
                        count += bfs(i, j, arr);
                    }
                }
                if (count == 0)
                    break;

                visit = new boolean[SIZE][SIZE];
                for (int[] pos : temp)
                    removeItem(pos[0], pos[1]);
                fillItem();
                maxRoundScore += count;
            }
            scoreList.add(maxRoundScore);
        }
        }


    public static int[] getExpectPosition(){
        // 출발점 찾기
        int max =0;
        int rx = -1;
        int ry = -1;
        int rd = -1;

        //우선 순위 고려해서 위치 찾기
        for (int d = 1; d < 4; ++d) {
            for (int j = 1; j < 4; ++j) {
                for (int i = 1; i < 4; ++i) {
                    int[][] rotateArr = rotate(i, j, d);
                    visit = new boolean[SIZE][SIZE];
                    temp.clear();
                    int score = 0;
                    for (int r = 0; r < 3; ++r) {
                        for (int c = 0; c < 3; ++c) {
                            if (!visit[r + i - 1][c + j - 1])
                                score += bfs(r + i - 1, c + j - 1, rotateArr);
                        }
                    }
                    if (score > max) {
                        list.clear();
                        list.addAll(temp);
                        rx = i;
                        ry = j;
                        rd = d;
                        max = score;
                    }

                }
            }
        }
        maxRoundScore += max;
        return new int[] {rx, ry, rd};
    }

    public static void removeItem(int x,int y){
        //보드에서 같은 값을 가진 인접한 숫자 삭제
        //bfs로 탐색
        visit = new boolean[SIZE][SIZE];
        visit[x][y] = true;
        int base = arr[x][y];
        arr[x][y] = 0;
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] {x,y});

        while(!q.isEmpty()){
            int[] pos = q.poll();
            for(int d =0; d<4; d++){
                int nx = pos[0] + dx[d];
                int ny = pos[1] + dy[d];
                if(!isInRange(nx,ny))continue;
                if(!visit[nx][ny] && arr[nx][ny] == base){
                    visit[nx][ny] = true;
                    arr[nx][ny] = 0;
                    q.add(new int[] {nx,ny});
                }
            }
        }
    }
    public static void fillItem(){
        //빈 보드 채우기
        for (int j = 0; j < SIZE; ++j) {
            for (int i = 4; i >= 0; --i)
                if (arr[i][j] == 0)
                    arr[i][j] = spare.poll();
        }
    }
    static int[] dx ={-1,0,1,0};
    static int[] dy = {0,1,0,-1};
    public static int bfs(int x, int y, int[][] rotateArr){
        int count = 1;
        visit[x][y] = true;
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[] {x,y});

        while (!q.isEmpty()){
            int[] pos = q.poll();
            for(int i = 0; i<4; i++){
                int nx = pos[0] + dx[i];
                int ny = pos[1] + dy[i];
                if(!isInRange(nx,ny)) continue;
                if(!visit[nx][ny] && rotateArr[nx][ny] == rotateArr[x][y]){
                    count ++;
                    visit[nx][ny] = true;
                    q.add(new int[] {nx,ny});
                }
            }
        }
        if(count >2){
            temp.add(new int[] {x,y});
            return count;
        }
        return 0;
    }

    public static int[][] rotate(int x, int y, int d) {
        //특정 좌표를 회전하고 회전된 배열 반환
        int[][] copy = new int[3][3];
        int[][] rotateArr = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j)
                rotateArr[i][j] = arr[i][j];
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j)
                if (d == 1) // 90도 회전
                    copy[i][j] = arr[3 - j + x - 2][i + y - 1];
                else if (d == 2) // 180도 회전
                    copy[i][j] = arr[3 - i + x - 2][3 - j + y - 2];
                else // 270도 회전
                    copy[i][j] = arr[j + x - 1][3 - i + y - 2];
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j)
                rotateArr[i + x - 1][j + y - 1] = copy[i][j];
        }
        return rotateArr;
    }
    public static boolean isInRange(int x, int y) {
        if (0 <= x && x < SIZE && 0 <= y && y < SIZE)
            return true;
        return false;
    }

    public static int stoi(String s){
        return Integer.parseInt(s);
    }
}