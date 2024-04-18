import matplotlib.pyplot as plt

def read_data(file_name):
    sizes = []
    times = []
    with open(file_name, 'r') as file:
        for line in file:
            size, time = map(float, line.strip().split())
            sizes.append(size)
            times.append(time)
    return sizes, times

def plot_graph(sizes, times):
    plt.plot(sizes, times, marker='o')
    plt.title('Boyut-Çalışma Zamanı Grafiği')
    plt.xlabel('Dosya Boyutu (Şehir Sayısı)')
    plt.ylabel('Çalışma Zamanı (milisaniye)')
    plt.grid(True)
    plt.show()

# Verileri dosyadan oku
file_name = 'outputs.txt'  # Değiştirilebilir, gerçek dosya adınıza göre ayarlayın
sizes, times = read_data(file_name)

# Grafiği çiz
plot_graph(sizes, times)
