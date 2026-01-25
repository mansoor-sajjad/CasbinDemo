import urllib.request
import urllib.parse
import sys

BASE_URL = "http://localhost:8080"

def login(username, tenant):
    url = f"{BASE_URL}/auth/login"
    # AuthController uses @RequestParam, so query params or form data.
    # Let's try form data (application/x-www-form-urlencoded) which urlencode produces.
    # But wait, @RequestParam usually binds query parameters.
    # If I send POST body as form data it works for standard servlets, but let's be sure.
    # Let's put them in query string to be 100% safe for @RequestParam on POST.
    # Actually, standard Spring MVC @RequestParam works with query params even on POST.
    
    params = urllib.parse.urlencode({'username': username, 'tenantId': tenant})
    url = f"{url}?{params}"
    
    req = urllib.request.Request(url, method='POST')
    try:
        with urllib.request.urlopen(req) as response:
            return response.read().decode('utf-8')
    except urllib.error.HTTPError as e:
        print(f"Login failed: {e.code} {e.read().decode()}")
        sys.exit(1)
    except Exception as e:
        print(f"Login error: {e}")
        sys.exit(1)

def get_shipments(token):
    url = f"{BASE_URL}/shipments"
    req = urllib.request.Request(url, method='GET')
    req.add_header('Authorization', f'Bearer {token}')
    try:
        with urllib.request.urlopen(req) as response:
            print(f"Status: {response.getcode()}")
            print(f"Response: {response.read().decode('utf-8')}")
    except urllib.error.HTTPError as e:
        print(f"Request failed: {e.code}")
        print(f"Response: {e.read().decode('utf-8')}")
    except Exception as e:
        print(f"Request error: {e}")

if __name__ == "__main__":
    print("Logging in as alice@tenant1...")
    token = login("alice", "tenant1")
    print(f"Token received ({len(token)} chars).")
    print("Requesting shipments...")
    get_shipments(token)
