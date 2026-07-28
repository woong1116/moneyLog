// api.js — 모든 화면에서 <script src="api.js"></script> 로 먼저 로드
// 빈 문자열 = 이 페이지를 서빙한 origin으로 요청 (localhost든 배포 서버든 자동으로 맞음)
const API_BASE = "";

// 저장된 토큰을 자동으로 Authorization 헤더에 첨부하는 fetch 래퍼.
// 백엔드는 성공/에러 모두 공통 응답 형식으로 응답한다:
//   성공: { success:true,  message, data, (meta) }
//   에러: { success:false, code, message, data:null }
// 이 헬퍼는 응답 형식을 검사한 뒤 성공이면 응답 전체(body)를 반환한다.
// → 호출부는 body.data(실제 데이터), body.meta(페이지 정보)에 접근한다.
async function apiFetch(path, options = {}) {
    const token = localStorage.getItem("accessToken");

    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {}),
    };
    if (token) {
        headers["Authorization"] = `Bearer ${token}`; // 토큰 자동 첨부
    }

    const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

    // 204(No Content)면 본문이 없으므로 그대로 null 반환
    if (res.status === 204) {
        return null;
    }

    // 응답 본문을 공통 응답 형식으로 파싱 (본문이 없을 수도 있으니 방어적으로 처리)
    const body = await res.json().catch(() => ({}));

    // 실패 판정: HTTP 상태가 실패거나, 응답의 success가 false인 경우
    if (!res.ok || body.success === false) {
        // 토큰 만료/미인증(401) 시 토큰 제거 후 로그인 화면으로 되돌리기
        if (res.status === 401) {
            localStorage.removeItem("accessToken");
            location.href = "login.html";
            return;
        }
        // 그 외 에러: 에러 응답 형식의 message/code를 활용
        throw new Error(body.message || `요청 실패 (${body.code || res.status})`);
    }

    // 성공: 응답 전체를 반환 → 호출부에서 body.data / body.meta 사용
    return body;
}

// 로그인 여부 간단 확인 — 각 보호 페이지 상단에서 호출
function requireLogin() {
    if (!localStorage.getItem("accessToken")) {
        location.href = "login.html";
    }
}