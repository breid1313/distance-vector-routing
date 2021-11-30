public class Entity0 extends Entity
{   
    // constant number of nodes
    int numEntities = NetworkSimulator.NUMENTITIES;
    // constant array of neighbor node indexes (to aid in iteration)
    int[] neighbors = new int[] {1, 2, 3};
    // Perform any necessary initialization in the constructor
    public Entity0()
    {
        // initialize all values in the DT to inf when the node is created
        for (int i = 0 ; i < numEntities; i++) {
            for (int j = 0; j < numEntities; j++) {
                distanceTable[i][j] = 999;
            }
        }

        // set the values in the distance table that we know from the diagram
        // TODO if time: would be nice to make this a little more dynamic. Hard code for now.
        // Could set neighbors nodes and edges up top and calculate this maybe.
        distanceTable[0][0] = 0;
        distanceTable[1][1] = 1;
        distanceTable[2][2] = 2;
        distanceTable[3][3] = 7;

        // store the known minimum distances from node0 to all over nodes
        int[] minDistances = new int[numEntities];
        for (int i = 0; i < numEntities; i++) {
            int minDist = 999;
            for (int j = 0; j < neighbors.length; j++) {
                if (distanceTable[i][neighbors[j]] < minDist) {
                    minDist = distanceTable[i][neighbors[j]];
                }
            }
            minDistances[i] = minDist;
        }

        // broadcast min distances to the other nodes
        for(int i = 0; i < neighbors.length; i++) {
            Packet packet = new Packet(0, neighbors[i], minDistances);
            NetworkSimulator.toLayer2(packet);
        }

        // debug/info logging
        System.out.println("Entity0 Initialized." + ". Distance Table is:\n");
        printDT();
    }
    
    // Handle updates when a packet is received.  Students will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what they
    // send to update.  Be careful to construct the source and destination of
    // the packet correctly.  Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p)
    {        
        // mark whether we need to broadcast an update to the rest of the nodes
        // if this becomes true we should send.
        boolean changed = false;

        // calculate our minimum costs, as in the init method
        // TODO would be nice to extract this and reuse...
        int[] minDistances = new int[numEntities];
        for (int i = 0; i < numEntities; i++) {
            int minDist = 999;
            for (int j = 0; j < neighbors.length; j++) {
                if (distanceTable[i][neighbors[j]] < minDist) {
                    minDist = distanceTable[i][neighbors[j]];
                }
            }
            minDistances[i] = minDist;
        }

        for(int i = 0; i < numEntities; i++){
            // update the distance table if it's cheaper to get to a node from p's source
            if ( p.getMincost(i) + minDistances[p.getSource()] < distanceTable[i][p.getSource()] ) {
                distanceTable[i][p.getSource()] = p.getMincost(i) + minDistances[p.getSource()];
                // update the min distance if we've found a cheaper route and mark
                // the we need to broadcast a change
                if (distanceTable[i][p.getSource()] < minDistances[i]) {
                    minDistances[i] = distanceTable[i][p.getSource()];
                    changed = true;
                  }
            }
        }

        // broadcast the change if one occurred during update
        if( changed ){
            System.out.println("Detected a min distance change at Entity0... broadcasting to other nodes.\n");
            for(int i = 0; i < neighbors.length; i++) {
                Packet packet = new Packet(0, neighbors[i], minDistances);
                NetworkSimulator.toLayer2(packet);
            }
        }

        // debug/info logging
        System.out.println("Entity0 Updated." + ". Distance Table is:\n");
        printDT();
    }
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
        // reset the distance table because we cannot assume that we have accurate information in the DT
        for (int i = 0 ; i < numEntities; i++) {
            for (int j = 0; j < numEntities; j++) {
                distanceTable[i][j] = 999;
            }
        }

        // set the values in the distance table that we know from the diagram
        // TODO if time: would be nice to make this a little more dynamic. Hard code for now.
        // Could set neighbors nodes and edges up top and calculate this maybe.
        distanceTable[0][0] = 0;
        distanceTable[1][1] = 1;
        distanceTable[2][2] = 2;
        distanceTable[3][3] = 7;

        // take in the new cost into the DT
        distanceTable[whichLink][whichLink] = newCost;

        // calc min distances as before
        // calculate our minimum costs, as in the init method
        // TODO would be nice to extract this and reuse...
        int[] minDistances = new int[numEntities];
        for (int i = 0; i < numEntities; i++) {
            int minDist = 999;
            for (int j = 0; j < neighbors.length; j++) {
                if (distanceTable[i][neighbors[j]] < minDist) {
                    minDist = distanceTable[i][neighbors[j]];
                }
            }
            minDistances[i] = minDist;
        }

        // broadcast the result to neighbors
        for(int i = 0; i < neighbors.length; i++) {
            Packet packet = new Packet(0, neighbors[i], minDistances);
            NetworkSimulator.toLayer2(packet);
        }

        // debug/info logging
        System.out.println("Entity0 Link Change Cost Completed." + " Distance Table is:\n");
        printDT();
    }
    
    public void printDT()
    {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D0 |   1   2   3");
        System.out.println("----+------------");
        for (int i = 1; i < NetworkSimulator.NUMENTITIES; i++)
        {
            System.out.print("   " + i + "|");
            for (int j = 1; j < NetworkSimulator.NUMENTITIES; j++)
            {
                if (distanceTable[i][j] < 10)
                {    
                    System.out.print("   ");
                }
                else if (distanceTable[i][j] < 100)
                {
                    System.out.print("  ");
                }
                else 
                {
                    System.out.print(" ");
                }
                
                System.out.print(distanceTable[i][j]);
            }
            System.out.println();
        }
    }
}