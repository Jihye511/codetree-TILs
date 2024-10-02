import java.io.*;
import java.util.*;

public class Main{
    public static final int SIZE =5;
    public static final int SIZE_SMALL =3;
    static int[][] arr;
    static Queue<Integer> spare;
    static int K,M;
    static boolean[][] visit;
    static int maxRoundScore;
    static List<int[]> list,temp;
    static List<Integer> scoreList;
    static int [] wall;
    static int[] dx = {-1,1,0,0};
    static int[] dy = {0,0,-1,1};
    static StringBuilder sb = new StringBuilder();
    public static void main(String[] args)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        K =Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        arr = new int[SIZE][SIZE];
        spare = new ArrayDeque<>();

        for(int i =0; i<SIZE; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j<SIZE; j++){
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        st = new StringTokenizer(br.readLine());
        for(int i =0; i<M; i++){
            spare.add(Integer.parseInt(st.nextToken()));
        }
        //1.격자 회전
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
        int[][] copy = new int[SIZE_SMALL][SIZE_SMALL];
        int[][] rotateArray = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                rotateArray[i][j] = arr[i][j];
            }
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
                rotateArray[i + x - 1][j + y - 1] = copy[i][j];
        }
        return rotateArray;
    }
    public static void fillItem(){
        //빈 보드 채우기
        for (int j = 0; j < SIZE; ++j) {
            for (int i = 4; i >= 0; --i)
                if (arr[i][j] == 0)
                    arr[i][j] = spare.poll();
        }
    }
    public static boolean isInRange(int x, int y) {
        if (0 <= x && x < SIZE && 0 <= y && y < SIZE)
            return true;
        return false;
    }
}