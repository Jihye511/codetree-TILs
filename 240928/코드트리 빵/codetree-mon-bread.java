import java.util.*;
import java.io.*;

class Pair{
    int x;
    int y;
    public Pair(int x, int y){
        this.x=x;
        this.y=y;
    }
    public boolean isSame(Pair p ){
        return this.x == p.x && this.y == p.y;
    }
}
public class Main{
        static int[][] map; // 0이면 빈 칸, 1이면 베이스캠프, 2면 지나갈 수 없음
        static int n, m, t=0;
        static Pair empty = new Pair(-1,-1);
        static Pair[] storeList;
        static Pair[] player;
        static boolean[] finalVisited;
        static boolean[][] visited; //bfs함수에서 사용
        static int[][] temp; //bfs 함수에서 사용
        static int[] nx = {-1,0,0,1};
        static int[] ny = {0,-1,1,0};
        public static void main(String[] args) throws IOException {
            //입력
            input();

            while(true){
                t+=1;

                //1. 플레이어 1칸 이동
                movePlayer();
                //2. 이동불가능한 칸 설정
                makeNoMove();
                //3. 베이스캠프로 이동
                moveToBasecamp();
                //0. 모든사람이 편의점에 도착하면 return;
                if(isArrived()) break;
            }
            //4. 시간 출력
            System.out.println(t);
        }
        public static void moveToBasecamp(){
            if(t<=m){
                int minX=-1;
                int minY= -1;
                int minDist = Integer.MAX_VALUE;
                //3.1 bfs 사용하여 편의점과 최닫거리 계산
                bfs(storeList[t]);
                //3.2 map이 1인 것들 중 거리가 작은 temp를 가진 idx값 찾기
                for(int i =0; i<n;i++){
                    for(int j =0; j<n; j++){
                        if(visited[i][j] && map[i][j] ==1 && minDist>temp[i][j]){
                            minDist = temp[i][j];
                            minX = i;
                            minY = j;
                        }
                    }
                }
                player[t] = new Pair(minX,minY);
                map[minX][minY] = 2;
            }
        }
        public static void makeNoMove(){
            for(int i =1; i<=m; i++){
                if(player[i].isSame(storeList[i])){
                    int px = player[i].x;
                    int py = player[i].y;
                    map[px][py] =2;
                    finalVisited[i] =true;
                }
            }
        }
        public static boolean isRange(int x, int y){
            return (x>=0 && x<n && y >=0 && y<n);
        }
        public static boolean canGo(int x, int y){
            //범위안에 있고 갈 수 있으면 true
            return isRange(x,y) && !visited[x][y] && map[x][y] !=2;
        }
        public static void movePlayer(){
            for(int i=1; i<=m; i++){
                //1.1 map 밖에 있거나 이미 편의점 도착한 사람이면 패스
                if(player[i] == empty || finalVisited[i]) continue;

                //1.2 편의점 위치로 부터 모든 좌표의 최단거리를 구하고 step에 저장해두기
                bfs(storeList[i]);

                //1.3.1 편의점 최단거리 방향 찾기
                int px = player[i].x;
                int py = player[i].y;
                int minX=-1,minY=-1;
                int minDist = Integer.MAX_VALUE;
                for(int d =0; d<4; d++){
                    int dx = px + nx[d];
                    int dy = py + ny[d];

                    if(isRange(dx,dy) && visited[dx][dy] && minDist>temp[dx][dy]){
                        minDist = temp[dx][dy];
                        minX = dx;
                        minY = dy;
                    }
                }
                //1.3.2 그 방향으로 이동
                player[i] = new Pair(minX,minY);
            }
        }
        public static void bfs(Pair storePos){
            //다 초기화
            for(int i =0; i<n; i++){
                for(int j =0; j<n; j++){
                    temp[i][j] =0;
                    visited[i][j] = false;
                }
            }
            //스토어부터 거리 계산
            Queue<Pair> q = new LinkedList<>();
            int sx = storePos.x, sy = storePos.y;
            q.offer(storePos);
            visited[sx][sy] =true;
            temp[sx][sy] = 0;

            while (!q.isEmpty()){
                Pair current = q.poll();

                //아직방문하지않은 칸 업데이트
                int x = current.x, y = current.y;
                for(int i =0; i<4; i++){
                    int dx = x + nx[i];
                    int dy = y + ny[i];
                    //다음 칸이 범위안에 있고 이동가능한 곳이지 확인
                    if(canGo(dx,dy) ){
                        visited[dx][dy] = true;
                        temp[dx][dy] = temp[x][y] +1;
                        q.offer(new Pair(dx,dy));
                    }
                }
            }

        }
        public static boolean isArrived(){
            for(int i =1; i<=m; i++) {
                if (!player[i].isSame(storeList[i])) {
                    return false;
                }
            }
            return true;
        }
        public static void input() throws IOException{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringTokenizer st = new StringTokenizer(br.readLine());

            n = Integer.parseInt(st.nextToken());
            m = Integer.parseInt(st.nextToken());
            map = new int[n][n];
            storeList = new Pair[m+1];
            player = new Pair[m+1];
            finalVisited = new boolean[m+1];
            temp = new int[n][n];
            visited = new boolean[n][n];
            for(int i =0; i<n; i++){
                st = new StringTokenizer(br.readLine());
                for(int j =0; j<n; j++){
                    map[i][j] = Integer.parseInt(st.nextToken());
                }
            }
            for(int i =1; i<=m; i++){
                st = new StringTokenizer(br.readLine());
                int x = Integer.parseInt(st.nextToken())-1;
                int y = Integer.parseInt(st.nextToken())-1;
                storeList[i] = new Pair(x,y);
                player[i] = new Pair(-1,-1);
                finalVisited[i] = false;
            }
        }
}