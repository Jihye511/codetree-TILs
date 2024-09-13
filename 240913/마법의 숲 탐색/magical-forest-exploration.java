import java.util.*;
import java.io.*;

public class Main{
    static int[][]map, golMap;
    static boolean[] visited;
    static int R,C,k,cnt,rMax;
    static int[] dx ={0,1,0,-1};
    static int[] dy ={-1, 0, 1, 0};
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        cnt =0;
        initMap();
        golMap = new int[k][];

        for(int i =0; i<k; i++){
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken()) -1; //인덱스로 나타내기
            int e = Integer.parseInt(st.nextToken());

            rMax = 0;
            visited = new boolean[k];
            south(i, -2, c, e);
        }
        System.out.println(cnt);

    }
    public static void south(int idx,int y, int x, int exit){
        while(true){
            //맨바닥에 도착했을때
            if(y == R-2 ) break;

            //남쪽으로 이동(내리기)
            if ((y == -2 && map[y + 2][x] == -1) || (map[y + 2][x] == -1 && map[y + 1][x - 1] == -1 && map[y + 1][x + 1] == -1)) {
                y += 1;
                continue;
            }
            //서쪽으로 이동
            if (x >= 2) {
                //중앙 한칸 아래와 왼쪽으로 한칸 아래이동 한 거 (총 5칸의 방문x 여부를 확인하는 것)
                if ((y == -2 && map[y + 2][x - 1] == -1) || ((y == -1) && map[y + 2][x - 1] == -1 && map[y + 1][x - 1] == -1 && map[y + 1][x - 2] == -1) || (map[y + 2][x - 1] == -1 && map[y + 1][x - 1] == -1 && map[y + 1][x - 2] == -1 && map[y][x - 2] == -1)) {
                    y += 1;
                    x -= 1;
                    exit = (exit + 3) % 4;
                    continue;
                }
            }

            //동쪽 이동
            if (x < C - 2) {
                if ((y== -2 && map[y + 2][x + 1] == -1) || ((y == -1) && map[y + 2][x + 1] == -1 && map[y + 1][x + 1] == -1 && map[y + 1][x + 2] == -1) || (map[y + 2][x + 1] == -1 && map[y + 1][x + 1] == -1 && map[y + 1][x + 2] == -1 && map[y][x + 2] == -1)) {
                    y += 1;
                    x += 1;
                    exit = (exit + 1) % 4;
                    continue;
                }
            }

            break;
        }
        //리셋하기
        if (y <= 0) {
            initMap();
            return;
        }
        //골렘 위치 저장
        map[y][x] = map[y - 1][x] = map[y][x + 1] = map[y + 1][x] = map[y][x - 1] = idx;
        golMap[idx] = new int[] {y, x, exit};

        //골렘 이동시키기
        moveGolem(idx);
        cnt += rMax;
    }

    public static void moveGolem(int idx){
        visited[idx] =true;
        //현재 골렘의 바닥위치 업데이트
        int ri = golMap[idx][0] +2;//주의 1아님
        rMax = Math.max(ri, rMax);

        //현재골렘의 출구 찾기
        int e = golMap[idx][2];
        ri = golMap[idx][0] + dy[e];
        int rj = golMap[idx][1] + dx[e];

        //출구근처에 있는 다른 골렘 찾기
        for (int d = 0; d < 4; d++) {
            int ni = ri + dy[d];
            int nj = rj + dx[d];

            if (ni < 0 || ni >= R || nj < 0 || nj >= C) continue;
            if (map[ni][nj] == -1 || visited[map[ni][nj]]) continue;

            moveGolem(map[ni][nj]);
        }
    }

    public static void initMap(){
        //비정형코드 사용하여 더 유연하게
        map = new int[R][];
        for (int i = 0; i < R; i++) {
            map[i] = new int[C];
            Arrays.fill(map[i], -1);
        }
    }

}