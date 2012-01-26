// Copyright 2011 Stephen Piccolo
// 
// This file is part of ML-Flex.
// 
// ML-Flex is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// any later version.
// 
// ML-Flex is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with ML-Flex. If not, see <http://www.gnu.org/licenses/>.

// Copyright 2011 Stephen Piccolo
// 
// This file is part of ML-Flex.
// 
// ML-Flex is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// any later version.
// 
// ML-Flex is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with ML-Flex. If not, see <http://www.gnu.org/licenses/>.

// Copyright 2011 Stephen Piccolo
// 
// This file is part of ML-Flex.
// 
// ML-Flex is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// any later version.
// 
// ML-Flex is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with ML-Flex. If not, see <http://www.gnu.org/licenses/>.

package mlflex.core;

import mlflex.helper.MiscUtilities;

import java.util.ArrayList;
import java.util.Collections;

/** This class contains logic for summarizing how features are ranked across multiple repetitions (for example, across multiple cross-validation folds).
 * @author Stephen Piccolo
 */
public class FeatureRanks
{
    private ArrayList<FeatureRank> _cumulativeRanks = new ArrayList<FeatureRank>();
    private int _numAdds = 0;

    /** Adds a list of ranked features.
     *
     * @param rankedFeatures List of ranked features
     */
    public void Add(ArrayList<String> rankedFeatures)
    {
        for (int i = 0; i < rankedFeatures.size(); i++)
            UpdateRank(new FeatureRank(rankedFeatures.get(i), i + 1));

        _numAdds++;
    }

    /** Calculates an average rank for each feature and returns them in order of their average rank.
     *
     * @return Features rank by their average rank (lowest first)
     */
    private FeatureRanks CalculateMeanRanks()
    {
        Collections.sort(_cumulativeRanks);

        FeatureRanks featureRanks = new FeatureRanks();
        featureRanks._numAdds = this._numAdds;

        for (int i = 0; i < this._cumulativeRanks.size(); i++)
        {
            FeatureRank featureRank = _cumulativeRanks.get(i);
            double mean = featureRank.Rank / (double) _numAdds;

            featureRanks.UpdateRank(new FeatureRank(featureRank.Feature, mean));
        }

        return featureRanks;
    }

    private void UpdateRank(FeatureRank featureRank)
    {
        if (_cumulativeRanks.contains(featureRank))
        {
            int index = _cumulativeRanks.indexOf(featureRank);
            FeatureRank existing = _cumulativeRanks.get(index);
            existing.Rank = existing.Rank + featureRank.Rank;
            _cumulativeRanks.set(index, existing);
        }
        else
            _cumulativeRanks.add(featureRank);
    }

    /** Calculates the mean feature rank across all ranks that have been added to this object.
     *
     * @throws Exception
     */
    public ArrayList<NameValuePair> GetMeanRanks() throws Exception
    {
        FeatureRanks meanRanks = CalculateMeanRanks();

        ArrayList<String> features = MiscUtilities.UnformatNames(GetAllFeatures(meanRanks));
        ArrayList<Double> ranks = GetAllRanks(meanRanks);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        for (int i=0; i<features.size(); i++)
            nameValuePairs.add(NameValuePair.Create(features.get(i), ranks.get(i)));

        return nameValuePairs;
    }

    private static ArrayList<String> GetAllFeatures(FeatureRanks featureRanks)
    {
        ArrayList<String> features = new ArrayList<String>();

        for (FeatureRank fr : featureRanks._cumulativeRanks)
            features.add(fr.Feature);

        return features;
    }

    private static ArrayList<Double> GetAllRanks(FeatureRanks featureRanks)
    {
        ArrayList<Double> ranks = new ArrayList<Double>();

        for (FeatureRank fr : featureRanks._cumulativeRanks)
            ranks.add(fr.Rank);

        return ranks;
    }
}
