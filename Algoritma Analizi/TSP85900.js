class KDNode {
    constructor(city, axis) {
        this.city = city;
        this.axis = axis;
        this.left = null;
        this.right = null;
    }
}

class KDTree {
    constructor(cities) {
        this.root = this.buildTree(cities, 0);
    }

    buildTree(cities, depth) {
        if (cities.length === 0) {
            return null;
        }
        const axis = depth % 2; // 2 boyutlu uzayda x ve y eksenlerini sırayla kullan
        cities.sort((a, b) => a[axis] - b[axis]);
        const medianIndex = Math.floor(cities.length / 2);
        const node = new KDNode(cities[medianIndex], axis);
        node.left = this.buildTree(cities.slice(0, medianIndex), depth + 1);
        node.right = this.buildTree(cities.slice(medianIndex + 1), depth + 1);
        return node;
    }

    findNearestCity(node, target, bestCity, bestDistance) {
        if (node === null) {
            return { bestCity, bestDistance };
        }
        const dist = distance(node.city, target);
        if (dist < bestDistance) {
            bestCity = node.city;
            bestDistance = dist;
        }
        const axis = node.axis;
        const axisDist = target[axis] - node.city[axis];
        const side = axisDist <= 0 ? 'left' : 'right';

        const nextNode = side === 'left' ? node.left : node.right;
        const otherNode = side === 'left' ? node.right : node.left;

        const nextBest = this.findNearestCity(nextNode, target, bestCity, bestDistance);
        bestCity = nextBest.bestCity;
        bestDistance = nextBest.bestDistance;

        if (Math.abs(axisDist) < bestDistance) {
            const otherBest = this.findNearestCity(otherNode, target, bestCity, bestDistance);
            bestCity = otherBest.bestCity;
            bestDistance = otherBest.bestDistance;
        }

        return { bestCity, bestDistance };
    }

    findNearest(target) {
        const initialBestCity = this.root.city;
        const initialBestDistance = distance(this.root.city, target);
        return this.findNearestCity(this.root, target, initialBestCity, initialBestDistance).bestCity;
    }
}

// KD-Tree kullanarak minimum mesafeyi bulma
function findNearestCityKDTree(cities, target) {
    const tree = new KDTree(cities);
    return tree.findNearest(target);
}

// Greedy algoritması kullanarak TSP problemi çözen fonksiyon (KD-Tree kullanarak minimum mesafeyi bulma)
function solveTSPWithKDTree(cities) {
    const n = cities.length;
    const visited = Array(n).fill(false);
    visited[0] = true;
    let current = 0;
    const path = [current];
    let cost = 0;
    for (let i = 0; i < n - 1; i++) {
        let minDist = Infinity;
        let nearestCity = null;
        for (let j = 0; j < n; j++) {
            if (!visited[j]) {
                const dist = distance(cities[current], findNearestCityKDTree(cities, cities[current]));
                if (dist < minDist) {
                    minDist = dist;
                    nearestCity = j;
                }
            }
        }
        visited[nearestCity] = true;
        path.push(nearestCity);
        cost += minDist;
        current = nearestCity;
    }
    cost += distance(cities[current], cities[0]); // İlk noktaya geri dön
    return { cost, path };
}

// Örnek dosya adı
const filename = 'tsp_51_1.txt';

// Veriyi oku
const { cities } = readData(filename);

// KD-Tree kullanarak Greedy algoritması ile TSP problemi çözme
let { cost, path } = solveTSPWithKDTree(cities);

// 2-Opt ile yolun iyileştirilmesi
path = improvePath_2Opt(cities, path);

// Maliyeti tekrar hesapla
cost = calculateCost(cities, path);

// Sonuçları yazdır
console.log('Optimal maliyet değeri (Greedy + 2-Opt + KD-Tree):', Math.round(cost));
console.log('Optimal maliyeti sağlayan path (Greedy + 2-Opt + KD-Tree):', path.join(' -> '));
